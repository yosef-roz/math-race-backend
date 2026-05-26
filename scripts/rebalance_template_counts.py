#!/usr/bin/env python3
"""
Rebalance templates across easy/medium/hard to satisfy minimum counts.

Goal (minimums):
- easy >= 20
- medium >= 30
- hard >= 20

Strategy:
- Keep "must be hard" templates in hard (percentages, fractions, ratios, speed/distance/time, rate-in/out).
- Everything else defaults to medium, with truly single-step going to easy.
- If easy still below minimum, promote simplest medium templates to easy.
- If hard above target minimum, demote non-hard (shouldn't happen due to must-hard).

IDs are rewritten to match the bucket: {difficulty}_logic_{i}.
"""

from __future__ import annotations

import json
import re
from dataclasses import dataclass
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DIR = ROOT / "src/main/resources/templates/math-questions"

MIN_COUNTS = {"easy": 20, "medium": 30, "hard": 20}


@dataclass(frozen=True)
class Tpl:
    questionTemplate: str
    answerTemplate: str
    hintTemplate: str
    distractorsTemplates: list[str]
    source_id: str


def load_all() -> list[Tpl]:
    out: list[Tpl] = []
    for diff in ("easy", "medium", "hard"):
        data = json.loads((DIR / f"{diff}.json").read_text(encoding="utf-8"))
        for o in data:
            out.append(
                Tpl(
                    questionTemplate=o["questionTemplate"],
                    answerTemplate=o["answerTemplate"],
                    hintTemplate=o["hintTemplate"],
                    distractorsTemplates=list(o["distractorsTemplates"]),
                    source_id=o.get("id", ""),
                )
            )
    return out


def normalize_key(t: Tpl) -> str:
    return re.sub(r"\s+", "", t.questionTemplate + "|" + t.answerTemplate)


def is_must_hard(t: Tpl) -> bool:
    s = (t.questionTemplate + " " + t.hintTemplate)
    sl = s.lower()

    # Percentages
    if "%" in s or "אחוז" in s or "div_100" in sl or "percent" in sl:
        return True

    # Fractions (explicit or via schema fields)
    if re.search(r"\b\d+/\d+\b", s):
        return True
    if any(w in s for w in ("שליש", "רבע", "חמישית", "חמישיות")):
        return True
    if any(m in sl for m in ("#denom", "#numer", "שבר:")):
        return True

    # Ratios
    if re.search(r"\b\d+\s*:\s*\d+\b", s):
        return True
    if any(m in s for m in ("#RATIO", "יחס", "פי ")):
        return True

    # Speed / distance / time style problems
    if any(m in s for m in ('קמ"ש', 'קמ\"ש', "מטרים בדקה")):
        return True
    if any(m in sl for m in ("#speed", "#speed1", "#speed2", "id=train", "id=car", "id=bicycle")):
        return True

    # In/out rates (pipes)
    if any(m in sl for m in ("#net_rate", "#rate_in", "#rate_out")):
        return True

    # Set theory / both/only markers
    if any(m in sl for m in ("#both", "#only_a", "#only_b")):
        return True

    return False


def op_score(t: Tpl) -> int:
    """Heuristic: higher means more complex."""
    s = t.questionTemplate
    score = 0
    score += len(re.findall(r"NUM:value=", s)) * 2
    score += len(re.findall(r":add_", s))
    score += len(re.findall(r":sub_", s))
    score += len(re.findall(r":mul_", s))
    score += len(re.findall(r":div_", s))
    score += len(re.findall(r"IF:\(", s))
    return score


def default_bucket(t: Tpl) -> str:
    if is_must_hard(t):
        return "hard"
    # single-step tends to have very low score and no chained value ops
    if op_score(t) <= 6 and len(re.findall(r"NUM:value=", t.questionTemplate)) <= 1:
        return "easy"
    return "medium"


def write_bucket(name: str, items: list[Tpl]) -> None:
    payload = []
    for i, t in enumerate(items):
        payload.append(
            {
                "id": f"{name}_logic_{i}",
                "questionTemplate": t.questionTemplate,
                "answerTemplate": t.answerTemplate,
                "hintTemplate": t.hintTemplate,
                "distractorsTemplates": t.distractorsTemplates,
            }
        )
    (DIR / f"{name}.json").write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main() -> None:
    all_items = load_all()
    # Dedup
    seen: set[str] = set()
    uniq: list[Tpl] = []
    for t in all_items:
        k = normalize_key(t)
        if k in seen:
            continue
        seen.add(k)
        uniq.append(t)

    buckets: dict[str, list[Tpl]] = {"easy": [], "medium": [], "hard": []}
    for t in uniq:
        buckets[default_bucket(t)].append(t)

    # Ensure minimum hard first: if below, steal highest-score medium
    if len(buckets["hard"]) < MIN_COUNTS["hard"]:
        needed = MIN_COUNTS["hard"] - len(buckets["hard"])
        buckets["medium"].sort(key=op_score, reverse=True)
        buckets["hard"].extend(buckets["medium"][:needed])
        buckets["medium"] = buckets["medium"][needed:]

    # Ensure minimum medium: if below, steal highest-score easy
    if len(buckets["medium"]) < MIN_COUNTS["medium"]:
        needed = MIN_COUNTS["medium"] - len(buckets["medium"])
        buckets["easy"].sort(key=op_score, reverse=True)
        buckets["medium"].extend(buckets["easy"][:needed])
        buckets["easy"] = buckets["easy"][needed:]

    # Ensure minimum easy: if below, steal lowest-score medium
    if len(buckets["easy"]) < MIN_COUNTS["easy"]:
        needed = MIN_COUNTS["easy"] - len(buckets["easy"])
        buckets["medium"].sort(key=op_score)
        buckets["easy"].extend(buckets["medium"][:needed])
        buckets["medium"] = buckets["medium"][needed:]

    # Final ordering: stable-ish, easiest to hardest within each bucket
    buckets["easy"].sort(key=op_score)
    buckets["medium"].sort(key=op_score)
    buckets["hard"].sort(key=op_score)

    for name in ("easy", "medium", "hard"):
        write_bucket(name, buckets[name])

    print("Rebalanced counts:")
    for name in ("easy", "medium", "hard"):
        print(f"  {name}: {len(buckets[name])}")


if __name__ == "__main__":
    main()

