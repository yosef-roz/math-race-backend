package com.example.math_race.race.questions;

import com.example.math_race.race.RacePlayer;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MathQuestionGenerator {


    static ArrayList<Human> humans =  new ArrayList<>();
    static ArrayList<Item> items =  new ArrayList<>();
    static {
        fillHumans();
    }


    String template = "[HUMAN:g=?:#1] [#1]";

    String t = "[ITEM:param=m;param=?:take:name]";



    public String gene(String template) {
        Set<String> tags = extractUniqueTags(template);
        Map<String, QuestionEntity> memory = new HashMap<>();
        String result = template;

        for (String tag : tags) {
            if (tag.contains(":") && !tag.startsWith("[#")) {
                TagInfo info = TagInfo.parse(tag);
                Map<String, String> resolvedConstraints = new HashMap<>();

                // פתרון סוגריים בתוך הפרמטרים
                for (Map.Entry<String, String> entry : info.getConstraints().entrySet()) {
                    resolvedConstraints.put(entry.getKey(), resolveValue(entry.getValue(), memory));
                }

                QuestionEntity chosen = null;
                if ("HUMAN".equals(info.getType())) chosen = findHuman(resolvedConstraints);
                else if ("ITEM".equals(info.getType())) chosen = findItem(resolvedConstraints);
                else if ("NUM".equals(info.getType())) chosen = findNumber(resolvedConstraints);

                if (chosen != null) {
                    memory.put(info.getId(), chosen);

                    String resolvedProp = resolveValue(info.getProperty(), memory);
                    result = result.replace(tag, chosen.getProperty(resolvedProp));
                }
            }else if (tag.startsWith("[#")) {
                if (tag.startsWith("[#")) {
                    TagInfo info = TagInfo.parse(tag);
                    if (memory.containsKey(info.getId())) {
                        QuestionEntity entity = memory.get(info.getId());

                        String resolvedProp = resolveValue(info.getProperty(), memory);
                        result = result.replace(tag, entity.getProperty(resolvedProp));
                    }
                }

            }
        }
        return result;
    }

    public Set<String> extractUniqueTags(String template) {
        Set<String> tags = new LinkedHashSet<>();

        int indexStart = -1;
        for (int i = 0; i < template.length(); i++) {
            if (template.charAt(i) == '[') {
                indexStart = i;
            } else if (template.charAt(i) == ']' && indexStart != -1) {
                String tag = template.substring(indexStart, i + 1);
                tags.add(tag);
                indexStart = -1;
            }
        }

        return tags;
    }

    private String resolveValue(String value, Map<String, QuestionEntity> memory) {
        // אם הערך לא מכיל סוגריים, הוא ערך רגיל (כמו "m" או "fruit")
        if (!value.startsWith("(") || !value.endsWith(")")) {
            return value;
        }

        // מורידים את הסוגריים: (#1:n) -> #1:n
        String expression = value.substring(1, value.length() - 1);
        String[] parts = expression.split(":");
        String id = parts[0]; // #1
        String property = (parts.length > 1) ? parts[1] : "";

        if (memory.containsKey(id)) {
            // שולפים מהאובייקט שבזיכרון את המאפיין המבוקש
            return memory.get(id).getProperty(property);
        }

        return value;
    }

    public Human findHuman(Map<String, String> constraints) {
        List<Human> matches = humans.stream()
                .filter(h -> h.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No human matches constraints: " + constraints);
            return null;
        }

        return matches.get(new Random().nextInt(matches.size()));
    }

    public Item findItem(Map<String, String> constraints) {
        List<Item> matches = items.stream()
                .filter(h -> h.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No item matches constraints: " + constraints);
            return null;
        }

        return matches.get(new Random().nextInt(matches.size()));
    }

    public NumberEntity findNumber(Map<String, String> constraints) {
        int min, max;

        try {
            min = Integer.parseInt(constraints.getOrDefault("min", "1"));
            max = Integer.parseInt(constraints.getOrDefault("max", "100"));

            if (min > max) {
                int temp = min;
                min = max;
                max = temp;
            }
        } catch (NumberFormatException e) {
            min = 1;
            max = 100;
        }

        int randomNumber = new Random().nextInt(max - min + 1) + min;
        return new NumberEntity(randomNumber);
    }

    public static void fillHumans(){
        humans.add(new Human("Shimon",Gender.MALE));
        humans.add(new Human("Noa",Gender.FEMALE));
    }


















    public MathQuestion generateForPlayer(RacePlayer player) {
        String expression = "המלך ביקש מעידן שיקנה לו 3 תפוחים, עידן קנה 3 תפוחים והביא מהבית עוד 2 ונתן הכל למלך. כמה תפוחים סהכ הביא עידן למלך ?";
        List<String> options = List.of("6","3","5","2");
        String correctAnswer = "5";
        int timeLimitSeconds = 15;
        int score = 20;

        return new MathQuestion(expression,options,correctAnswer,timeLimitSeconds,score);
    }
}
