#!/usr/bin/env python3
"""Extract newTemplates from MainIdan.java and emit categorized JSON files."""
import json
import re
from pathlib import Path

JAVA_FILE = Path(__file__).resolve().parents[1] / "src/main/java/com/example/math_race/MainIdan.java"
OUT_DIR = Path(__file__).resolve().parents[1] / "src/main/resources/templates/math-questions"


def unescape_java(s: str) -> str:
    return (
        s.replace("\\\"", '"')
        .replace("\\n", "\n")
        .replace("\\t", "\t")
        .replace("\\\\", "\\")
    )


def extract_quoted_parts(text: str) -> str:
    """Join all Java string literals in a field (handles 'a' + 'b' + ...)."""
    parts = re.findall(r'"((?:\\.|[^"\\])*)"', text)
    return "".join(unescape_java(p) for p in parts)


def split_top_level_commas(s: str) -> list[str]:
    """Split by commas not inside parentheses or strings."""
    fields = []
    current = []
    depth = 0
    in_string = False
    escape = False
    for c in s:
        if in_string:
            current.append(c)
            if escape:
                escape = False
            elif c == "\\":
                escape = True
            elif c == '"':
                in_string = False
        else:
            if c == '"':
                in_string = True
                current.append(c)
            elif c == "(":
                depth += 1
                current.append(c)
            elif c == ")":
                depth = max(0, depth - 1)
                current.append(c)
            elif c == "," and depth == 0:
                fields.append("".join(current).strip())
                current = []
            else:
                current.append(c)
    if current:
        fields.append("".join(current).strip())
    return fields


def extract_templates(content: str) -> list[list[str]]:
    start = content.find("List<String[]> newTemplates = new ArrayList<>();")
    if start == -1:
        raise RuntimeError("newTemplates list not found")
    content = content[start:]

    results = []
    for m in re.finditer(r"newTemplates\.add\(new String\[\]\{", content):
        i = m.end()
        depth = 1
        buf = []
        in_string = False
        escape = False
        while i < len(content) and depth > 0:
            c = content[i]
            if in_string:
                buf.append(c)
                if escape:
                    escape = False
                elif c == "\\":
                    escape = True
                elif c == '"':
                    in_string = False
            else:
                if c == '"':
                    in_string = True
                    buf.append(c)
                elif c == "{":
                    depth += 1
                    buf.append(c)
                elif c == "}":
                    depth -= 1
                    if depth == 0:
                        break
                    buf.append(c)
                else:
                    buf.append(c)
            i += 1

        inner = "".join(buf)
        raw_fields = split_top_level_commas(inner)
        if len(raw_fields) != 6:
            continue
        fields = [extract_quoted_parts(f) for f in raw_fields]
        # Skip commented placeholders
        if fields[0].startswith("שאלה") and fields[1] == "תשובה":
            continue
        if not fields[0] or "[PLACE:" not in fields[0] and "[HUMAN:" not in fields[0] and "[NUM:" not in fields[0]:
            if not fields[0].startswith("[") and not fields[0].startswith("ל-"):
                continue
        results.append(fields)
    return results


def normalize_key(block: list[str]) -> str:
    return re.sub(r"\s+", "", block[0] + "|" + block[1])


def count_value_ops(text: str) -> int:
    return len(re.findall(r"NUM:value=", text))


def categorize(q: str, hint: str) -> str:
    t = (q + " " + hint)
    tl = t.lower()

    # HARD
    hard_signals = [
        "אחוזים", "%", "#percent", "div_100",
        "div_3):", "motion", "motion", "motion", "div_4):", "div_5):",
        "1/3", "1/4", "1/5", "2/5", "שליש", "רבע", "חמישית", "חמישיות",
        "חלקי [#", "שבר:", "#denom", "#numer", ":#parts]",
        "#r_blue", "#r_red", ":#ratio]", "פי [#ratio]",
        ":#a]:[#b]", "ל-[#r_blue]",
        "#speed", "קמ\\\"ש", 'קמ"ש', "מהירות", "#lap_len", "#km_per",
        "id=train", "id=car", "id=bicycle", "#v1]", "#v2]",
        "#only_a", "#both]", "#net_rate", "#rate_in", "#rate_out",
        "#num_bulk", "#bulk_price",
        "#avg1", "#avg2",
        "#speed1", "#speed2",
        "#profit_p", "#rev_total",
        "#hundreds", "glass_b",
        "#a_carpet", "#uncovered",
        ":#food]", "#clothes]",
        "#diff_years",
        "#fut_b",
    ]
    for sig in hard_signals:
        if sig.lower() in tl or sig in t:
            return "hard"

    if count_value_ops(q) >= 4:
        return "hard"

    # MEDIUM
    medium_signals = [
        "#area", "#perim", "#perimeter", "#vol",
        "היקף", "שטח", "מ\"ר", "סמ\"ר", "נפח",
        "#avg]", "ממוצע",
        "#per_hour", "#base_done", "#total_goal",
        "#rows]", "#per_row]", "#total_items", "#broken",
        "#total_dist", "#goal]", "#remaining",
        "#total_weight", "#max_capacity",
        "#tables]", "#arranged]",
        "#weekly", "#weeks]", "#walked]",
        "#daily]", "#total_pages", "#read]",
        "#mins_per", "#total_mins", "#t_start",
        "#in_box", "#boxes]", "#target]",
        "#sum_r", "#share_a",
        "#w_val:mul_3",
        "#r_sum", "#ra]", "#rb]",
        "#total_cost]", "#price_a",
        ":mul_(#price))",
        "#disc]", "div_5):*:#disc",  # 1/5 discount fraction -> medium/hard border; treat as hard via div_5 in hard
        "#collected]", "#target]",  # inventory goal
        "#sum_future", "#years]",
        "#bonus]",  # mul days then add bonus
        "#base_box:mul_", "#new_box:mul_", "#per_box]",
        "#mid:sub_(#gave)",  # got then gave away
    ]
    for sig in medium_signals:
        if sig.lower() in tl or sig in t:
            return "medium"

    if count_value_ops(q) >= 2:
        if re.search(r"mul_\([^)]+\).*(add_|sub_)", q) or re.search(
            r"(add_|sub_)\([^)]+\).*(mul_|motion)", q
        ):
            return "medium"

    return "easy"


def main():
    content = JAVA_FILE.read_text(encoding="utf-8")
    blocks = extract_templates(content)
    print(f"Parsed {len(blocks)} valid templates")

    seen = {}
    unique = []
    for b in blocks:
        k = normalize_key(b)
        if k not in seen:
            seen[k] = True
            unique.append(b)

    print(f"Unique templates: {len(unique)}")

    buckets = {"easy": [], "medium": [], "hard": []}
    counters = {"easy": 0, "medium": 0, "hard": 0}

    for q, ans, d1, d2, d3, hint in unique:
        diff = categorize(q, hint)
        idx = counters[diff]
        counters[diff] += 1
        buckets[diff].append(
            {
                "id": f"{diff}_logic_{idx}",
                "questionTemplate": q,
                "answerTemplate": ans,
                "hintTemplate": hint,
                "distractorsTemplates": [d1, d2, d3],
            }
        )

    for diff in ("easy", "medium", "hard"):
        path = OUT_DIR / f"{diff}.json"
        with path.open("w", encoding="utf-8") as f:
            json.dump(buckets[diff], f, ensure_ascii=False, indent=2)
            f.write("\n")
        print(f"{diff}: {len(buckets[diff])} -> {path}")


if __name__ == "__main__":
    main()
