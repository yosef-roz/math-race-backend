package com.example.math_race.questionGenerator;

import com.example.math_race.questionGenerator.tags.core.MatchableTag;
import com.example.math_race.questionGenerator.tags.core.TemplateTag;
import com.example.math_race.questionGenerator.tags.core.TagInfo;
import com.example.math_race.questionGenerator.tags.types.*;
import com.example.math_race.race.questions.MathQuestion;
import com.example.math_race.race.questions.MathQuestionGenerator;
import com.example.math_race.repositories.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.math_race.questionGenerator.tags.core.TagInfo.smartSplit;
import static com.example.math_race.questionGenerator.tags.enums.Gender.FEMALE;
import static com.example.math_race.questionGenerator.tags.enums.Gender.MALE;
import static com.example.math_race.questionGenerator.tags.enums.ItemCategory.*;
import static com.example.math_race.questionGenerator.tags.enums.UnitType.COUNT;

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
        ensureTemplateCoverageItems();
        this.verbs = dictionaryRepository.loadVerbTag();
        this.places = dictionaryRepository.loadPlaceTag();
        this.adjectives = dictionaryRepository.loadAdjectiveTag();
        this.units = dictionaryRepository.loadUnitTag();
        ensureTemplateCoverageUnits();
        this.roles = dictionaryRepository.loadRoleTag();

        System.out.println("✅ QuestionEngine: Dictionary loaded successfully to memory!");
    }

    public void fill() {
        this.humans = MathQuestionGenerator.fillHumans();
        this.items = MathQuestionGenerator.fillItems();
        ensureTemplateCoverageItems();
        this.verbs = MathQuestionGenerator.fillVerbs();
        this.places = MathQuestionGenerator.fillPlaces();
        this.adjectives = MathQuestionGenerator.fillAdjectives();
        this.units = MathQuestionGenerator.fillUnits();
        ensureTemplateCoverageUnits();
        this.roles = MathQuestionGenerator.fillRoles();

        System.out.println("✅ QuestionEngine: Dictionary loaded successfully to memory!");
    }

    public QuestionEngine(){
        fill();
        this.dictionaryRepository = null;
    }

    public MathQuestion processTemplate(QuestionTemplate questionTemplate) {
        Map<String, TemplateTag> memory = new HashMap<>();

        String questionText = evaluateTemplate(questionTemplate.questionTemplate(), memory);
        String correctAnswer = evaluateTemplate(questionTemplate.answerTemplate(), memory);

        List<String> options = new ArrayList<>();
        options.add(correctAnswer);

        if (questionTemplate.distractorsTemplates() != null) {
            for (String distractor : questionTemplate.distractorsTemplates()) {

                String distractorValue = evaluateTemplate(distractor, memory);
                while (options.contains(distractorValue)) {
                    try {
                        int val = Integer.parseInt(distractorValue);
                        distractorValue = String.valueOf(val + 1);
                    } catch (NumberFormatException e) {
                        distractorValue = distractorValue + " ";
                        break;
                    }
                }

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
        mathQuestion.setExpression(questionText + " תשובה נכונה  : " + correctAnswer);
        mathQuestion.setCorrectAnswer(correctAnswer);
        mathQuestion.setOptions(options);
        mathQuestion.setHint(evaluateTemplate(questionTemplate.hintTemplate(), memory));
        mathQuestion.setScore(score);
        mathQuestion.setTimeLimitSeconds(timeLimit);

        return mathQuestion;
    }

    public String evaluateTemplate(String template, Map<String, TemplateTag> memory) {
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

                TemplateTag chosen = switch (info.getType()) {
                    case "HUMAN" -> getRandomMatch(humans,resolvedConstraints, HumanTag.class);
                    case "ITEM" -> getRandomMatch(items,resolvedConstraints, ItemTag.class);
                    case "NUM" -> findNumber(resolvedConstraints);
                    case "VERB" -> getRandomMatch(verbs,resolvedConstraints, VerbTag.class);
                    case "PLACE" -> getRandomMatch(places,resolvedConstraints, PlaceTag.class);
                    case "ADJ" -> getRandomMatch(adjectives,resolvedConstraints, AdjectiveTag.class);
                    case "UNIT" -> getRandomMatch(units,resolvedConstraints, UnitTag.class);
                    case "TIME" -> findTime(resolvedConstraints);
                    case "ROLE" -> getRandomMatch(roles,resolvedConstraints, RoleTag.class);
                    default -> null;
                };

                if (chosen != null) {
                    String tagId = info.getId();
                    boolean isTemp = false;

                    if (tagId == null || tagId.isEmpty()) {
                        tagId = "#TEMP_" + java.util.UUID.randomUUID().toString().substring(0, 6);
                        isTemp = true;
                    }

                    memory.put(tagId, chosen);
                    String resolvedProp = resolveValue("("+ tagId + ":" + info.getProperty() + ")", memory);

                    result = result.replace(tag, !info.getProperty().equals("*") ? resolvedProp : "");

                    if (isTemp) memory.remove(tagId);
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

    private String resolveValue(String value, Map<String, TemplateTag> memory) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String expression = value.trim();
        boolean isNot = false;

        if (expression.startsWith("!") && isFullyWrappedByParentheses(expression.substring(1))) {
            isNot = true;
            expression = expression.substring(2, expression.length() - 1);
        } else if (isFullyWrappedByParentheses(expression)) {
            expression = expression.substring(1, expression.length() - 1);
        } else {
            return value;
        }

        List<String> plusSplit = smartSplit(expression, '+');

        if (plusSplit.size() > 1) {
            StringBuilder combinedResult = new StringBuilder();
            for (String part : plusSplit) {
                combinedResult.append(resolveValue(part.trim(), memory));
            }
            return isNot ? "!" + combinedResult.toString() : combinedResult.toString();
        }

        String resolvedSingle = resolveValue(plusSplit.get(0).trim(), memory);

        if (resolvedSingle.startsWith("#")) {
            String[] parts = resolvedSingle.split(":", 2);
            String id = parts[0].trim();
            String property = (parts.length > 1) ? parts[1].trim() : "";

            if (memory.containsKey(id)) {

                String[] splitNewProperty = splitBeforeAndWithinParentheses(property);
                if (splitNewProperty != null) {

                    property = splitNewProperty[0] + resolveValue(splitNewProperty[1], memory);
                }

                String finalValue = memory.get(id).getProperty(property);
                if (finalValue == null) finalValue = "";

                return isNot ? "!" + finalValue : finalValue;
            } else {
                System.out.println("the id : " + id + " does not exist in the memory");
            }
        }

        return isNot ? "!" + resolvedSingle : resolvedSingle;
    }

    private String processIfTag(String tag, String result, Map<String, TemplateTag> memory){
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

            String[] opInfo = findMainOperator(conditionFromTag);

            if (opInfo != null && opInfo.length == 2) {

                String operator =  opInfo[0];
                int opIndex =  Integer.parseInt(opInfo[1]);

                String leftSide = conditionFromTag.substring(0, opIndex).trim();
                leftSide = resolveValue(leftSide,memory);

                String rightSide = conditionFromTag.substring(opIndex + operator.length()).trim();
                rightSide = resolveValue(rightSide,memory);

                boolean conditionMet = false;
                if (operator.equals("=")) {
                    conditionMet = leftSide.equals(rightSide);
                } else if (operator.equals("!=")) {
                    conditionMet = !leftSide.equals(rightSide);
                } else {
                    try {
                        double actualNum = Double.parseDouble(leftSide);
                        double expectedNum = Double.parseDouble(rightSide);
                        conditionMet = switch (operator) {
                            case ">" -> actualNum > expectedNum;
                            case "<" -> actualNum < expectedNum;
                            case ">=" -> actualNum >= expectedNum;
                            case "<=" -> actualNum <= expectedNum;
                            default -> false;
                        };
                    } catch (NumberFormatException nfe) {
                        System.out.println("Warning: Numeric comparison failed for actual: [" + leftSide + "] and expected: [" + rightSide + "]");
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

    private <T extends MatchableTag> T getRandomMatch(List<T> dataList, Map<String, String> constraints, Class<T> clazz) {
        List<T> matches = dataList.stream()
                .filter(item -> item.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No " + clazz.getSimpleName() + " matches constraints: " + constraints);
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    private void ensureTemplateCoverageItems() {
        if (this.items == null) {
            return;
        }

        Set<com.example.math_race.questionGenerator.tags.enums.UnitType> countOnly = Set.of(COUNT);
        Set<String> ids = new HashSet<>();
        for (ItemTag item : this.items) {
            ids.add(item.getId());
        }

        List<ItemTag> extras = List.of(
                // MONEY (COUNT)
                new ItemTag("banknote", "שטר", "שטרות", MALE, countOnly, MONEY),
                new ItemTag("coin", "מטבע", "מטבעות", MALE, countOnly, MONEY),
                // ENTERTAINMENT (COUNT)
                new ItemTag("ticket", "כרטיס", "כרטיסים", MALE, countOnly, ENTERTAINMENT),
                new ItemTag("game_card", "כרטיס משחק", "כרטיסי משחק", MALE, countOnly, ENTERTAINMENT),
                // CLOTHING (COUNT)
                new ItemTag("hat", "כובע", "כובעים", MALE, countOnly, CLOTHING),
                new ItemTag("jacket", "ז'קט", "ז'קטים", MALE, countOnly, CLOTHING),
                // FOOD (COUNT)
                new ItemTag("cookie", "עוגייה", "עוגיות", FEMALE, countOnly, FOOD),
                new ItemTag("apple_count", "תפוח", "תפוחים", MALE, countOnly, FOOD)
        );

        for (ItemTag extra : extras) {
            if (!ids.contains(extra.getId())) {
                this.items.add(extra);
            }
        }
    }

    private void ensureTemplateCoverageUnits() {
        if (this.units == null) {
            this.units = new ArrayList<>();
        } else {
            this.units = new ArrayList<>(this.units);
        }

        Set<String> ids = new HashSet<>();
        for (UnitTag unit : this.units) {
            ids.add(unit.getProperty("id"));
        }

        List<UnitTag> extras = List.of(
                new UnitTag("unit_pair_clothing", "זוג", "זוגות", MALE, COUNT, CLOTHING),
                new UnitTag("unit_item_clothing", "יחידה", "יחידות", FEMALE, COUNT, CLOTHING),
                new UnitTag("unit_pack_clothing", "מארז", "מארזים", MALE, COUNT, CLOTHING)
        );

        for (UnitTag extra : extras) {
            if (!ids.contains(extra.getProperty("id"))) {
                this.units.add(extra);
            }
        }
    }

    private NumberTag findNumber(Map<String, String> constraints) {
        int min = 1;
        int max = 100;

        String minStr = constraints.get("min");
        if (minStr != null && !minStr.trim().equals("?")) {
            try { min = Integer.parseInt(minStr.trim()); }
            catch (NumberFormatException e) { System.out.println("Warning: Invalid min format."); }
        }

        String maxStr = constraints.get("max");
        if (maxStr != null && !maxStr.trim().equals("?")) {
            try { max = Integer.parseInt(maxStr.trim()); }
            catch (NumberFormatException e) { System.out.println("Warning: Invalid max format."); }
        }

        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }

        String valStr = constraints.getOrDefault("value", "?").trim();
        return new NumberTag(valStr, min, max);
    }

    private TimeTag findTime(Map<String, String> constraints) {
        int minMinutes = 0;    // 00:00
        int maxMinutes = 1439; // 23:59

        String minStr = constraints.get("min");
        if (minStr != null && !minStr.trim().equals("?")) {
            try { minMinutes = TimeTag.parseTime(minStr.trim()); }
            catch (Exception e) { System.out.println("Warning: Invalid min time format."); }
        }

        String maxStr = constraints.get("max");
        if (maxStr != null && !maxStr.trim().equals("?")) {
            try { maxMinutes = TimeTag.parseTime(maxStr.trim()); }
            catch (Exception e) { System.out.println("Warning: Invalid max time format."); }
        }

        if (minMinutes > maxMinutes) {
            int temp = minMinutes;
            minMinutes = maxMinutes;
            maxMinutes = temp;
        }

        boolean round = !constraints.getOrDefault("round", "true").equalsIgnoreCase("false");
        String valStr = constraints.getOrDefault("value", "?").trim();

        return new TimeTag(valStr, minMinutes, maxMinutes, round);
    }

    private static boolean isFullyWrappedByParentheses(String s) {
        if (s == null || s.length() < 2) return false;
        if (s.charAt(0) != '(' || s.charAt(s.length() - 1) != ')') return false;

        int depth = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;

            if (depth == 0 && i < s.length() - 1) {
                return false;
            }
        }
        return depth == 0;
    }

    private static String[] splitBeforeAndWithinParentheses(String str) {
        if (str == null || !str.endsWith(")")) {
            return null;
        }

        int firstOpen = str.indexOf('(');

        if (firstOpen == -1) {
            return null;
        }

        String x = str.substring(0, firstOpen);
        String y = str.substring(firstOpen);

        return new String[]{x, y};
    }

    private static String[] findMainOperator(String condition) {
        String[] operators = {">=", "<=", "!=", ">", "<", "="};
        int depth = 0;

        for (int i = 0; i < condition.length(); i++) {
            char c = condition.charAt(i);

            if (c == '(') depth++;
            else if (c == ')') depth--;

            if (depth == 0) {
                for (String op : operators) {
                    if (condition.startsWith(op, i)) {
                        return new String[]{op, String.valueOf(i)};
                    }
                }
            }
        }
        return null;
    }
}