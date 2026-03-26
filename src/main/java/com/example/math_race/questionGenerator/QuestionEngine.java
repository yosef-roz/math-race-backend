package com.example.math_race.questionGenerator;

import com.example.math_race.questionGenerator.tags.core.QuestionEntity;
import com.example.math_race.questionGenerator.tags.core.TagInfo;
import com.example.math_race.questionGenerator.tags.types.*;
import com.example.math_race.race.questions.MathQuestion;
import com.example.math_race.repositories.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class QuestionEngine {

    private final DictionaryRepository dictionaryRepository;

    private List<HumanTag> humans;
    private List<ItemTag> items;
    private List<VerbTag> verbs;
    private List<PlaceTag> places;
    private List<AdjectiveTag> adjectives;
    private List<UnitTag> units;
    private List<RoleTag> roles;

    @Autowired
    public QuestionEngine(DictionaryRepository dictionaryRepository){
        this.dictionaryRepository = dictionaryRepository;
    }

    @PostConstruct
    public void initDictionaryCache() {
        this.humans = dictionaryRepository.loadHumanTags();
        this.items = dictionaryRepository.loadItemTag();
        this.verbs = dictionaryRepository.loadVerbTag();
        this.places = dictionaryRepository.loadPlaceTag();
        this.adjectives = dictionaryRepository.loadAdjectiveTag();
        this.units = dictionaryRepository.loadUnitTag();
        this.roles = dictionaryRepository.loadRoleTag();

        System.out.println("✅ QuestionEngine: Dictionary loaded successfully to memory!");
    }

    public MathQuestion processTemplate(QuestionTemplate questionTemplate) {
        Map<String, QuestionEntity> memory = new HashMap<>();

        String questionText = evaluateTemplate(questionTemplate.questionTemplate(), memory);
        String correctAnswer = evaluateTemplate(questionTemplate.answerTemplate(), memory);

        List<String> options = new ArrayList<>();
        options.add(correctAnswer);

        if (questionTemplate.distractorsTemplates() != null) {
            for (String distractor : questionTemplate.distractorsTemplates()) {

                // לבדוק זאת

                String distractorValue = evaluateTemplate(distractor, memory);

                // --- מנגנון חכם למניעת כפילויות בתשובות (Anti-Collision) ---
                while (options.contains(distractorValue)) {
                    try {
                        // אם המספר הזה כבר קיים, נוסיף לו 1 כדי ליצור מסיח קרוב אך שונה
                        int val = Integer.parseInt(distractorValue);
                        distractorValue = String.valueOf(val + 1);
                    } catch (NumberFormatException e) {
                        // אם במקרה מדובר בטקסט שחוזר על עצמו, נוסיף לו סימן כדי לשנות אותו
                        distractorValue = distractorValue + " ";
                        break;
                    }
                }

                // התיקון: מוסיפים את המשתנה המתוקן, ולא קוראים לפונקציה מחדש!
                options.add(distractorValue);
            }
        }

        Collections.shuffle(options);

        int score = 0;
        int timeLimit = 0;
        String id = questionTemplate.id().toLowerCase();

        if (id.startsWith("easy")) {
            score = 10;
            timeLimit = 15;
        } else if (id.startsWith("medium")) {
            score = 30;
            timeLimit = 30;
        } else if (id.startsWith("hard")) {
            score = 100;
            timeLimit = 60;
        }

        MathQuestion mathQuestion = new MathQuestion();
        mathQuestion.setId(questionTemplate.id());
        mathQuestion.setExpression(questionText);
        mathQuestion.setCorrectAnswer(correctAnswer);
        mathQuestion.setOptions(options);
        mathQuestion.setHint(evaluateTemplate(questionTemplate.hintTemplate(), memory));
        mathQuestion.setScore(score);
        mathQuestion.setTimeLimitSeconds(timeLimit);

        return mathQuestion;
    }

    private String evaluateTemplate(String template, Map<String, QuestionEntity> memory) {
        Set<String> tags = extractUniqueTags(template);

        if (memory == null) {
            memory = new HashMap<>();
        }
        String result = template;

        for (String tag : tags) {

            if (tag.startsWith("[IF:")) {
                result = processIfTag(tag, result, memory);
                continue;
            }

            TagInfo info = TagInfo.parse(tag);
            if (!tag.startsWith("[#")) {
                Map<String, String> resolvedConstraints = new HashMap<>();

                for (Map.Entry<String, String> entry : info.getConstraints().entrySet()) {
                    resolvedConstraints.put(entry.getKey(), resolveValue(entry.getValue(), memory));
                }

                QuestionEntity chosen = switch (info.getType()) {
                    case "HUMAN" -> findHuman(resolvedConstraints);
                    case "ITEM" -> findItem(resolvedConstraints);
                    case "NUM" -> findNumber(resolvedConstraints);
                    case "VERB" -> findVerb(resolvedConstraints);
                    case "PLACE" -> findPlace(resolvedConstraints);
                    case "ADJ" -> findAdjective(resolvedConstraints);
                    case "UNIT" -> findUnit(resolvedConstraints);
                    case "TIME" -> findTime(resolvedConstraints);
                    case "ROLE" -> findRole(resolvedConstraints);
                    default -> null;
                };

                if (chosen != null) {
                    memory.put(info.getId(), chosen);

                    String resolvedProp = resolveValue( "("+ info.getId()+ ":" + info.getProperty() + ")", memory);
                    result = result.replace(tag, !info.getProperty().equals("*") ? resolvedProp : "");
                }
            } else {
                if (memory.containsKey(info.getId())) {

                    String resolvedProp = resolveValue("(" + tag.substring(1, tag.length() - 1) + ")", memory);
                    result = result.replace(tag, resolvedProp);
                }
            }
        }
        return result;
    }

    private Set<String> extractUniqueTags(String template) {
        Set<String> tags = new LinkedHashSet<>();

        int indexStart = -1;
        int depth = 0;

        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);

            if (c == '[') {
                if (depth == 0) {
                    indexStart = i;
                }
                depth++;

            } else if (c == ']') {
                if (depth > 0) {
                    depth--;

                    if (depth == 0) {
                        String tag = template.substring(indexStart, i + 1);
                        tags.add(tag);
                        indexStart = -1;
                    }
                }
            }
        }
        return tags;
    }

    private String resolveValue(String value, Map<String, QuestionEntity> memory) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        boolean isNot = false;
        String expression = value.trim();

        if (expression.startsWith("!(") && expression.endsWith(")")) {
            isNot = true;
            expression = expression.substring(2, expression.length() - 1);
        } else if (expression.startsWith("(") && expression.endsWith(")")) {
            expression = expression.substring(1, expression.length() - 1);
        } else {
            return value;
        }

        String[] parts = expression.split(":", 2);
        String id = parts[0];
        String property = (parts.length > 1) ? parts[1] : "";


        if (memory.containsKey(id)) {
            String newProperty = splitBeforeAndWithinParentheses(property);
            String[] splitNewProperty;

            if (newProperty != null) {
                splitNewProperty = newProperty.split(":",2);
                property = splitNewProperty[0] + resolveValue(splitNewProperty[1], memory);
            }

            String resolvedValue = memory.get(id).getProperty(property);
            return isNot ? "!" + resolvedValue : resolvedValue;
        }

        return value;
    }

    private String splitBeforeAndWithinParentheses(String str) {
        if (str == null || !str.endsWith(")")) {
            return null;
        }

        int firstOpen = str.indexOf('(');

        if (firstOpen == -1) {
            return null;
        }

        String x = str.substring(0, firstOpen);
        String y = str.substring(firstOpen);

        return x + ":" + y;
    }

    private String processIfTag(String tag, String result, Map<String, QuestionEntity> memory){
        try {
            int originalCondEnd = tag.indexOf(":<");
            if (originalCondEnd == -1) return result;

            String identifier = tag.substring(0, originalCondEnd + 2);
            int startIdx = result.indexOf(identifier);
            if (startIdx == -1) return result;

            int depth = 0;
            int endIdx = -1;
            for (int i = startIdx; i < result.length(); i++) {
                if (result.charAt(i) == '[') depth++;
                else if (result.charAt(i) == ']') {
                    depth--;
                    if (depth == 0) {
                        endIdx = i;
                        break;
                    }
                }
            }
            if (endIdx == -1) return result;

            String currentTag = result.substring(startIdx, endIdx + 1);
            int currentCondEnd = currentTag.indexOf(":<");

            int branchSplit = -1;
            int innerDepth = 0;
            for (int i = currentCondEnd + 2; i < currentTag.length(); i++) {
                if (currentTag.charAt(i) == '[') innerDepth++;
                else if (currentTag.charAt(i) == ']') innerDepth--;

                if (innerDepth == 0 && currentTag.startsWith(">:<", i)) {
                    branchSplit = i;
                    break;
                }
            }
            if (branchSplit == -1) return result;

            String conditionFromTag = currentTag.substring(4, currentCondEnd).trim();
            String trueOption = currentTag.substring(currentCondEnd + 2, branchSplit);
            String falseOption = currentTag.substring(branchSplit + 3, currentTag.length() - 2);

            String operator = "";
            if (conditionFromTag.contains(">=")) operator = ">=";
            else if (conditionFromTag.contains("<=")) operator = "<=";
            else if (conditionFromTag.contains("!=")) operator = "!=";
            else if (conditionFromTag.contains(">")) operator = ">";
            else if (conditionFromTag.contains("<")) operator = "<";
            else if (conditionFromTag.contains("=")) operator = "=";

            if (!operator.isEmpty()) {
                int opIndex = conditionFromTag.indexOf(operator);
                String leftSide = conditionFromTag.substring(0, opIndex).trim();
                String expectedStr = conditionFromTag.substring(opIndex + operator.length()).trim();

                expectedStr = expectedStr.startsWith("(") ? resolveValue(expectedStr,memory) : expectedStr;

                String actualStr = "";

                if (leftSide.startsWith("(")) {
                    actualStr = resolveValue(leftSide, memory);
                } else {
                    String entityId = leftSide;
                    String propertyKey = "";

                    if (leftSide.contains(":")) {
                        String[] varParts = leftSide.split(":", 2);
                        entityId = varParts[0].trim();
                        propertyKey = varParts[1].trim();
                    }

                    if (memory.containsKey(entityId)) {
                        actualStr = memory.get(entityId).getProperty(propertyKey);
                    } else {
                        actualStr = leftSide;
                    }
                }

                actualStr = actualStr != null ? actualStr : "";
                expectedStr = expectedStr != null ? expectedStr : "";

                boolean conditionMet = false;
                if (operator.equals("=")) {
                    conditionMet = actualStr.equals(expectedStr);
                } else if (operator.equals("!=")) {
                    conditionMet = !actualStr.equals(expectedStr);
                } else {
                    try {
                        double actualNum = Double.parseDouble(actualStr);
                        double expectedNum = Double.parseDouble(expectedStr);
                        conditionMet = switch (operator) {
                            case ">" -> actualNum > expectedNum;
                            case "<" -> actualNum < expectedNum;
                            case ">=" -> actualNum >= expectedNum;
                            case "<=" -> actualNum <= expectedNum;
                            default -> conditionMet;
                        };
                    } catch (NumberFormatException nfe) {
                        System.out.println("Warning: Numeric comparison failed for actual: [" + actualStr + "] and expected: [" + expectedStr + "]");
                    }
                }

                String chosenText = conditionMet ? trueOption : falseOption;
                String resolvedText = evaluateTemplate(chosenText, memory);

                result = result.replace(currentTag, resolvedText);
            }
        } catch (Exception e) {
            System.out.println("Error parsing IF tag: " + tag);
        }

        return result;
    }

    private HumanTag findHuman(Map<String, String> constraints) {
        List<HumanTag> matches = humans.stream()
                .filter(h -> h.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No human matches constraints: " + constraints);
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    private QuestionEntity findAdjective(Map<String, String> constraints) {
        List<AdjectiveTag> matches = adjectives.stream()
                .filter(a -> a.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No adjective matches constraints: " + constraints);
            return null;
        }

        AdjectiveTag chosenAdjective = matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));

        String g = constraints.getOrDefault("g", "MALE").toUpperCase();
        String num = constraints.getOrDefault("num", "s").toLowerCase();

        String exactWord = chosenAdjective.getWord(g, num);

        return key -> exactWord;
    }

    private ItemTag findItem(Map<String, String> constraints) {
        List<ItemTag> matches = items.stream()
                .filter(h -> h.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No item matches constraints: " + constraints);
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }


    private PlaceTag findPlace(Map<String, String> constraints) {
        List<PlaceTag> matches = places.stream()
                .filter(p -> p.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No place matches constraints: " + constraints);
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    private NumberTag findNumber(Map<String, String> constraints) {
        int min, max;
        try {
            min = Integer.parseInt(constraints.getOrDefault("min", "1").trim());
            max = Integer.parseInt(constraints.getOrDefault("max", "100").trim());

            if (min > max) {
                int temp = min;
                min = max;
                max = temp;
            }
        } catch (NumberFormatException e) {
            min = 1;
            max = 100;
        }

        if (constraints.containsKey("value") && !constraints.get("value").equals("?")) {
            String valStr = constraints.get("value").trim();

            if (valStr.startsWith("!")) {
                try {
                    int forbiddenValue = Integer.parseInt(valStr.substring(1));

                    if (min == max && min == forbiddenValue) {
                        return new NumberTag(min);
                    }

                    int randomNumber;
                    do {
                        randomNumber = java.util.concurrent.ThreadLocalRandom.current().nextInt(min, max + 1);
                    } while (randomNumber == forbiddenValue);

                    return new NumberTag(randomNumber);
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Invalid value: " + valStr);
                }
            }

            else {
                try {
                    return new NumberTag(Integer.parseInt(valStr));
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Invalid value for number: " + valStr);
                }
            }
        }

        int randomNumber = java.util.concurrent.ThreadLocalRandom.current().nextInt(min, max + 1);
        return new NumberTag(randomNumber);
    }

    private TimeTag findTime(Map<String, String> constraints) {
        int minMinutes = 0;
        int maxMinutes = 1439;

        try {
            if (constraints.containsKey("min") && !constraints.get("min").equals("?")) {
                minMinutes = parseTime(constraints.get("min"));
            }
            if (constraints.containsKey("max") && !constraints.get("max").equals("?")) {
                maxMinutes = parseTime(constraints.get("max"));
            }
        } catch (Exception e) {
            System.out.println("Warning: Invalid time format in min/max constraints");
        }

        if (minMinutes > maxMinutes) {
            int temp = minMinutes;
            minMinutes = maxMinutes;
            maxMinutes = temp;
        }

        boolean round = !constraints.getOrDefault("round", "true").equalsIgnoreCase("false");

        if (constraints.containsKey("value") && !constraints.get("value").equals("?")) {
            String valStr = constraints.get("value").trim();

            if (valStr.startsWith("!")) {
                try {
                    int forbiddenValue = parseTime(valStr.substring(1));

                    if (minMinutes == maxMinutes && minMinutes == forbiddenValue) {
                        return new TimeTag(minMinutes);
                    }

                    int randomMinutes;
                    int attempts = 0;

                    do {
                        randomMinutes = java.util.concurrent.ThreadLocalRandom.current().nextInt(minMinutes, maxMinutes + 1);

                        if (round) {
                            randomMinutes = Math.round(randomMinutes / 5.0f) * 5;
                            if (randomMinutes > maxMinutes) randomMinutes = maxMinutes;
                            if (randomMinutes < minMinutes) randomMinutes = minMinutes;
                        }

                        attempts++;
                        if (attempts > 100) break;

                    } while (randomMinutes == forbiddenValue);

                    return new TimeTag(randomMinutes);

                } catch (Exception e) {
                    System.out.println("Warning: Invalid time format for forbidden value: " + valStr);
                }
            }
            else {
                try {
                    return new TimeTag(parseTime(valStr));
                } catch (Exception e) {
                    System.out.println("Warning: Invalid time format for specific value: " + valStr);
                }
            }
        }

        int randomMinutes = java.util.concurrent.ThreadLocalRandom.current().nextInt(minMinutes, maxMinutes + 1);

        if (round) {
            randomMinutes = Math.round(randomMinutes / 5.0f) * 5;
            if (randomMinutes > maxMinutes) randomMinutes = maxMinutes;
            if (randomMinutes < minMinutes) randomMinutes = minMinutes;
        }

        return new TimeTag(randomMinutes);
    }

    private static int parseTime(String timeStr) {
        String[] parts = timeStr.trim().split("[:.]");
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        return h * 60 + m;
    }

    private QuestionEntity findVerb(Map<String, String> constraints) {
        List<VerbTag> matches = verbs.stream()
                .filter(v -> v.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No verb matches constraints: " + constraints);
            return null;
        }

        VerbTag chosenVerb = matches.get(ThreadLocalRandom.current().nextInt(matches.size()));

        String f = constraints.getOrDefault("f", "regular").toLowerCase();
        String t = constraints.getOrDefault("t", "past").toLowerCase();
        String g = constraints.getOrDefault("g", "MALE").toUpperCase();
        String num = constraints.getOrDefault("num", "s").toLowerCase();

        if (f.equals("inf")) {
            return key -> chosenVerb.getWord("inf", "ANY", "ANY");
        }

        String exactWord = chosenVerb.getWord(t, g, num);

        return key -> exactWord;
    }

    private QuestionEntity findUnit(Map<String, String> constraints) {
        List<UnitTag> matches = units.stream()
                .filter(u -> u.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No unit matches constraints: " + constraints);
            return null;
        }

        return matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));
    }

    private QuestionEntity findRole(Map<String, String> constraints) {
        List<RoleTag> matches = roles.stream()
                .filter(r -> r.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No role matches constraints: " + constraints);
            return null;
        }

        return matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));
    }
}
