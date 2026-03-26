package com.example.math_race.race.questions;

import com.example.math_race.questionGenerator.tags.core.QuestionEntity;
import com.example.math_race.questionGenerator.tags.core.TagInfo;
import com.example.math_race.questionGenerator.tags.enums.*;
import com.example.math_race.questionGenerator.tags.types.*;
import com.example.math_race.race.RacePlayer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MathQuestionGenerator {


    static ArrayList<HumanTag> humans;
    static ArrayList<ItemTag> items;
    static ArrayList<VerbTag> verbs;
    static ArrayList<PlaceTag> places;
    public static List<AdjectiveTag> adjectives;
    public static List<UnitTag> units;
    private static final List<RoleTag> roles;

    static {
        humans = fillHumans();
        verbs = fillVerbs();
        items = fillItems();
        places = fillPlaces();
        adjectives = fillAdjectives();
        units = fillUnits();
        roles = fillRoles();
    }


    String template = "[HUMAN:g=?:#1] [VERB:id=buy;g=(#1:g);t=past;num=s:#2] [NUM:min=2;max=10:mul_3:#3] [ITEM:type=FOOD:p:#4]";
// זה יכול להדפיס: "Noa קנתה 5 apples" או "Shimon קנה 3 bananas"

    String t = "[ITEM:param=m;param=?:take:name]";



    public static String gene(String template, Map<String, QuestionEntity> memory) {
        Set<String> tags = extractUniqueTags(template);

        if (memory == null) {
            memory = new HashMap<>();
        }
        String result = template;

        for (String tag : tags) {

            if (tag.startsWith("[IF:")) {
                try {
                    int originalCondEnd = tag.indexOf(":<");
                    if (originalCondEnd == -1) continue;

                    String identifier = tag.substring(0, originalCondEnd + 2);
                    int startIdx = result.indexOf(identifier);
                    if (startIdx == -1) continue;

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
                    if (endIdx == -1) continue;

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
                    if (branchSplit == -1) continue;

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
                        String resolvedText = gene(chosenText, memory);

                        result = result.replace(currentTag, resolvedText);
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing IF tag: " + tag);
                }
                continue;
            }
            TagInfo info = TagInfo.parse(tag);
            if (!tag.startsWith("[#")) {
                Map<String, String> resolvedConstraints = new HashMap<>();

                for (Map.Entry<String, String> entry : info.getConstraints().entrySet()) {
                    resolvedConstraints.put(entry.getKey(), resolveValue(entry.getValue(), memory));
                }

                QuestionEntity chosen = null;
                if ("HUMAN".equals(info.getType())) chosen = findHuman(resolvedConstraints);
                else if ("ITEM".equals(info.getType())) chosen = findItem(resolvedConstraints);
                else if ("NUM".equals(info.getType())) chosen = findNumber(resolvedConstraints);
                else if ("VERB".equals(info.getType())) chosen = findVerb(resolvedConstraints);
                else if ("PLACE".equals(info.getType())) chosen = findPlace(resolvedConstraints);
                else if ("ADJ".equals(info.getType())) chosen = findAdjective(resolvedConstraints);
                else if ("UNIT".equals(info.getType())) chosen = findUnit(resolvedConstraints);
                else if ("TIME".equals(info.getType())) chosen = findTime(resolvedConstraints);
                else if ("ROLE".equals(info.getType())) chosen = findRole(resolvedConstraints);

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

    public static Set<String> extractUniqueTags(String template) {
        Set<String> tags = new LinkedHashSet<>();

        int indexStart = -1;
        int depth = 0; // המונה שמציל אותנו

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

    private static String resolveValue(String value, Map<String, QuestionEntity> memory) {
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


    public static String splitBeforeAndWithinParentheses(String str) {
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


    public static HumanTag findHuman(Map<String, String> constraints) {
        List<HumanTag> matches = humans.stream()
                .filter(h -> h.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No human matches constraints: " + constraints);
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static QuestionEntity findAdjective(Map<String, String> constraints) {
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

    public static ItemTag findItem(Map<String, String> constraints) {
        List<ItemTag> matches = items.stream()
                .filter(h -> h.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No item matches constraints: " + constraints);
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static PlaceTag findPlace(Map<String, String> constraints) {
        List<PlaceTag> matches = places.stream()
                .filter(p -> p.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No place matches constraints: " + constraints); // שונה ל-place
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static NumberTag findNumber(Map<String, String> constraints) {
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

    public static TimeTag findTime(Map<String, String> constraints) {
        int minMinutes = 0;       // 00:00 (ברירת מחדל למינימום)
        int maxMinutes = 1439;    // 23:59 (ברירת מחדל למקסימום)

        // 1. קריאת גבולות המינימום והמקסימום
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

        // סידור הגבולות למקרה שהזינו הפוך
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

    public static QuestionEntity findVerb(Map<String, String> constraints) {
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

    public static QuestionEntity findUnit(Map<String, String> constraints) {
        List<UnitTag> matches = units.stream()
                .filter(u -> u.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No unit matches constraints: " + constraints);
            return null;
        }

        return matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static QuestionEntity findRole(Map<String, String> constraints) {
        List<RoleTag> matches = roles.stream() // בהנחה שיש לך רשימה סטטית של תפקידים שנקראת roles
                .filter(r -> r.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No role matches constraints: " + constraints);
            return null;
        }

        // בחירה אקראית מתוך התפקידים שעברו את הסינון
        return matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static ArrayList<HumanTag> fillHumans() {
        ArrayList<HumanTag> humans = new ArrayList<>();
        // --- בנים (25) ---
        humans.add(new HumanTag("שמעון", Gender.MALE));
        humans.add(new HumanTag("יוסף", Gender.MALE));
        humans.add(new HumanTag("אברהם", Gender.MALE));
        humans.add(new HumanTag("דוד", Gender.MALE));
        humans.add(new HumanTag("משה", Gender.MALE));
        humans.add(new HumanTag("אריאל", Gender.MALE));
        humans.add(new HumanTag("נועם", Gender.MALE));
        humans.add(new HumanTag("איתי", Gender.MALE));
        humans.add(new HumanTag("אורי", Gender.MALE));
        humans.add(new HumanTag("עומר", Gender.MALE));
        humans.add(new HumanTag("דניאל", Gender.MALE));
        humans.add(new HumanTag("יהונתן", Gender.MALE));
        humans.add(new HumanTag("רועי", Gender.MALE));
        humans.add(new HumanTag("עידן", Gender.MALE));
        humans.add(new HumanTag("עמית", Gender.MALE));
        humans.add(new HumanTag("גיא", Gender.MALE));
        humans.add(new HumanTag("מאור", Gender.MALE));
        humans.add(new HumanTag("תומר", Gender.MALE));
        humans.add(new HumanTag("אלעד", Gender.MALE));
        humans.add(new HumanTag("ירון", Gender.MALE));
        humans.add(new HumanTag("אורן", Gender.MALE));
        humans.add(new HumanTag("ברק", Gender.MALE));
        humans.add(new HumanTag("גלעד", Gender.MALE));
        humans.add(new HumanTag("ניר", Gender.MALE));
        humans.add(new HumanTag("אסף", Gender.MALE));

        // --- בנות (25) ---
        humans.add(new HumanTag("נועה", Gender.FEMALE));
        humans.add(new HumanTag("תמר", Gender.FEMALE));
        humans.add(new HumanTag("יעל", Gender.FEMALE));
        humans.add(new HumanTag("מאיה", Gender.FEMALE));
        humans.add(new HumanTag("אביגיל", Gender.FEMALE));
        humans.add(new HumanTag("טליה", Gender.FEMALE));
        humans.add(new HumanTag("עדי", Gender.FEMALE));
        humans.add(new HumanTag("שירה", Gender.FEMALE));
        humans.add(new HumanTag("מיכל", Gender.FEMALE));
        humans.add(new HumanTag("רוני", Gender.FEMALE));
        humans.add(new HumanTag("אלה", Gender.FEMALE));
        humans.add(new HumanTag("עדן", Gender.FEMALE));
        humans.add(new HumanTag("הילה", Gender.FEMALE));
        humans.add(new HumanTag("דנה", Gender.FEMALE));
        humans.add(new HumanTag("מורן", Gender.FEMALE));
        humans.add(new HumanTag("קרן", Gender.FEMALE));
        humans.add(new HumanTag("ענת", Gender.FEMALE));
        humans.add(new HumanTag("מירי", Gender.FEMALE));
        humans.add(new HumanTag("גלי", Gender.FEMALE));
        humans.add(new HumanTag("סיון", Gender.FEMALE));
        humans.add(new HumanTag("רותם", Gender.FEMALE)); // מתאים גם לבנים, אבל הגדרנו כבת
        humans.add(new HumanTag("שיר", Gender.FEMALE));
        humans.add(new HumanTag("אור", Gender.FEMALE));   // כנ"ל
        humans.add(new HumanTag("שחר", Gender.FEMALE));   // כנ"ל
        humans.add(new HumanTag("ענבל", Gender.FEMALE));

        return  humans;
    }

    public static ArrayList<ItemTag> fillItems() {
        ArrayList<ItemTag>  items = new ArrayList<>();

        Set<UnitType> weightOnly = Set.of(UnitType.WEIGHT);
        Set<UnitType> volumeOnly = Set.of(UnitType.VOLUME);
        Set<UnitType> countOnly = Set.of(UnitType.COUNT);
        Set<UnitType> lengthOnly = Set.of(UnitType.LENGTH);
        Set<UnitType> weightOrCount = Set.of(UnitType.WEIGHT, UnitType.COUNT);
        Set<UnitType> volumeOrCount = Set.of(UnitType.VOLUME, UnitType.COUNT);
        Set<UnitType> noUnit = Set.of(UnitType.NONE);

        // --- מזון ושתייה ---
        items.add(new ItemTag("תפוח", "תפוחים", Gender.MALE, weightOrCount, ItemCategory.PRODUCE));
        items.add(new ItemTag("עגבנייה", "עגבניות", Gender.FEMALE, weightOrCount, ItemCategory.PRODUCE));
        items.add(new ItemTag("לחם", "כיכרות לחם", Gender.MALE, countOnly, ItemCategory.BAKED_GOODS));
        items.add(new ItemTag("קרואסון", "קרואסונים", Gender.MALE, countOnly, ItemCategory.BAKED_GOODS));
        items.add(new ItemTag("מים", "בקבוקי מים", Gender.MALE, volumeOrCount, ItemCategory.DRINKS));
        items.add(new ItemTag("סוכרייה", "סוכריות", Gender.FEMALE, weightOrCount, ItemCategory.SWEETS));
        items.add(new ItemTag("פופקורן", "פופקורן", Gender.MALE, weightOrCount, ItemCategory.SWEETS));
        items.add(new ItemTag("קמח", "שקי קמח", Gender.MALE, weightOnly, ItemCategory.GENERAL_FOOD));

        // --- ART SUPPLIES (יצירה - חדש!) ---
        items.add(new ItemTag("מכחול", "מכחולים", Gender.MALE, countOnly, ItemCategory.ART_SUPPLIES));
        items.add(new ItemTag("קנבס", "קנבסים", Gender.MALE, countOnly, ItemCategory.ART_SUPPLIES));
        items.add(new ItemTag("שפופרת צבע", "שפופרות צבע", Gender.FEMALE, countOnly, ItemCategory.ART_SUPPLIES));
        items.add(new ItemTag("חימר", "גושי חימר", Gender.MALE, weightOrCount, ItemCategory.ART_SUPPLIES));

        // --- STATIONERY (כלי כתיבה) ---
        items.add(new ItemTag("עיפרון", "עפרונות", Gender.MALE, countOnly, ItemCategory.STATIONERY));
        items.add(new ItemTag("מחברת", "מחברות", Gender.FEMALE, countOnly, ItemCategory.STATIONERY));
        items.add(new ItemTag("מחק", "מחקים", Gender.MALE, countOnly, ItemCategory.STATIONERY));
        items.add(new ItemTag("סרגל", "סרגלים", Gender.MALE, countOnly, ItemCategory.STATIONERY));

        // --- ANTIQUE & COLLECTIBLE (עתיקות ואספנות) ---
        items.add(new ItemTag("מטבע זהב", "מטבעות זהב", Gender.MALE, weightOrCount, ItemCategory.ANTIQUE, ItemCategory.COLLECTIBLE));
        items.add(new ItemTag("כד חרס", "כדי חרס", Gender.MALE, countOnly, ItemCategory.ANTIQUE));
        items.add(new ItemTag("קלף פוקימון", "קלפי פוקימון", Gender.MALE, countOnly, ItemCategory.COLLECTIBLE));
        items.add(new ItemTag("בובת פופ", "בובות פופ", Gender.FEMALE, countOnly, ItemCategory.COLLECTIBLE));

        // --- MEDICAL (רפואי) ---
        items.add(new ItemTag("תרופה", "תרופות", Gender.FEMALE, countOnly, ItemCategory.MEDICAL));
        items.add(new ItemTag("פלסטר", "פלסטרים", Gender.MALE, countOnly, ItemCategory.MEDICAL));

        // --- TOY (צעצועים) ---
        items.add(new ItemTag("בובה", "בובות", Gender.FEMALE, countOnly, ItemCategory.TOY));
        items.add(new ItemTag("פאזל", "פאזלים", Gender.MALE, countOnly, ItemCategory.TOY));

        // --- CLOTHING (ביגוד) ---
        items.add(new ItemTag("חולצה", "חולצות", Gender.FEMALE, noUnit, ItemCategory.CLOTHING));
        items.add(new ItemTag("מכנס", "מכנסיים", Gender.MALE, countOnly, ItemCategory.CLOTHING));

        // --- ELECTRONICS (אלקטרוניקה) ---
        items.add(new ItemTag("מחשב נייד", "מחשבים ניידים", Gender.MALE, countOnly, ItemCategory.ELECTRONICS));
        items.add(new ItemTag("אוזנייה", "אוזניות", Gender.FEMALE, countOnly, ItemCategory.ELECTRONICS));

        // --- HARDWARE (חומרי בניין ועבודה - התאמה לאורך) ---
        items.add(new ItemTag("כבל חשמל", "כבלי חשמל", Gender.MALE, lengthOnly, ItemCategory.HARDWARE, ItemCategory.ELECTRONICS));
        items.add(new ItemTag("חבל", "חבלים", Gender.MALE, lengthOnly, ItemCategory.HARDWARE));
        items.add(new ItemTag("צינור", "צינורות", Gender.MALE, lengthOnly, ItemCategory.HARDWARE));

        return  items;
    }



    public static ArrayList<VerbTag> fillVerbs() {
        ArrayList<VerbTag> verbs = new ArrayList<>();
        // --- קנה ---
        VerbTag buy = new VerbTag("buy");
        buy.addForm("past", "MALE", "s", "קנה");
        buy.addForm("past", "FEMALE", "s", "קנתה");
        buy.addForm("past", "MALE", "p", "קנו");
        buy.addForm("past", "FEMALE", "p", "קנו");
        buy.addForm("inf", "ANY", "ANY", "לקנות");
        verbs.add(buy);

        // --- אכל (לאוכל, מאפים וממתקים) ---
        VerbTag eat = new VerbTag("eat");
        eat.addForm("past", "MALE", "s", "אכל");
        eat.addForm("past", "FEMALE", "s", "אכלה");
        eat.addForm("past", "MALE", "p", "אכלו");
        eat.addForm("past", "FEMALE", "p", "אכלו");
        eat.addForm("inf", "ANY", "ANY", "לאכול");
        verbs.add(eat);

        // --- שתה (חדש - למשקאות!) ---
        VerbTag drink = new VerbTag("drink");
        drink.addForm("past", "MALE", "s", "שתה");
        drink.addForm("past", "FEMALE", "s", "שתתה");
        drink.addForm("past", "MALE", "p", "שתו");
        drink.addForm("past", "FEMALE", "p", "שתו");
        drink.addForm("inf", "ANY", "ANY", "לשתות");
        verbs.add(drink);

        // --- לבש (חדש - לביגוד!) ---
        VerbTag wear = new VerbTag("wear");
        wear.addForm("past", "MALE", "s", "לבש");
        wear.addForm("past", "FEMALE", "s", "לבשה");
        wear.addForm("past", "MALE", "p", "לבשו");
        wear.addForm("past", "FEMALE", "p", "לבשו");
        wear.addForm("inf", "ANY", "ANY", "ללבוש");
        verbs.add(wear);

        // --- תיקן (חדש - לטמבורייה ואלקטרוניקה!) ---
        VerbTag fix = new VerbTag("fix");
        fix.addForm("past", "MALE", "s", "תיקן");
        fix.addForm("past", "FEMALE", "s", "תיקנה");
        fix.addForm("past", "MALE", "p", "תיקנו");
        fix.addForm("past", "FEMALE", "p", "תיקנו");
        fix.addForm("inf", "ANY", "ANY", "לתקן");
        verbs.add(fix);

        // --- נתן ---
        VerbTag give = new VerbTag("give");
        give.addForm("past", "MALE", "s", "נתן");
        give.addForm("past", "FEMALE", "s", "נתנה");
        give.addForm("past", "MALE", "p", "נתנו");
        give.addForm("past", "FEMALE", "p", "נתנו");
        give.addForm("inf", "ANY", "ANY", "לתת");
        verbs.add(give);

        // --- קיבל ---
        VerbTag receive = new VerbTag("receive");
        receive.addForm("past", "MALE", "s", "קיבל");
        receive.addForm("past", "FEMALE", "s", "קיבלה");
        receive.addForm("past", "MALE", "p", "קיבלו");
        receive.addForm("past", "FEMALE", "p", "קיבלו");
        receive.addForm("inf", "ANY", "ANY", "לקבל");
        verbs.add(receive);

        // --- מצא ---
        VerbTag find = new VerbTag("find");
        find.addForm("past", "MALE", "s", "מצא");
        find.addForm("past", "FEMALE", "s", "מצאה");
        find.addForm("past", "MALE", "p", "מצאו");
        find.addForm("past", "FEMALE", "p", "מצאו");
        find.addForm("inf", "ANY", "ANY", "למצוא");
        verbs.add(find);

        // --- איבד ---
        VerbTag lose = new VerbTag("lose");
        lose.addForm("past", "MALE", "s", "איבד");
        lose.addForm("past", "FEMALE", "s", "איבדה");
        lose.addForm("past", "MALE", "p", "איבדו");
        lose.addForm("past", "FEMALE", "p", "איבדו");
        lose.addForm("inf", "ANY", "ANY", "לאבד");
        verbs.add(lose);

        // --- אסף ---
        VerbTag collect = new VerbTag("collect");
        collect.addForm("past", "MALE", "s", "אסף");
        collect.addForm("past", "FEMALE", "s", "אספה");
        collect.addForm("past", "MALE", "p", "אספו");
        collect.addForm("past", "FEMALE", "p", "אספו");
        collect.addForm("inf", "ANY", "ANY", "לאסוף");
        verbs.add(collect);

        // --- חילק ---
        VerbTag divide = new VerbTag("divide");
        divide.addForm("past", "MALE", "s", "חילק");
        divide.addForm("past", "FEMALE", "s", "חילקה");
        divide.addForm("past", "MALE", "p", "חילקו");
        divide.addForm("past", "FEMALE", "p", "חילקו");
        divide.addForm("inf", "ANY", "ANY", "לחלק");
        verbs.add(divide);

        // --- נכנס ---
        VerbTag enter = new VerbTag("enter");
        enter.addForm("past", "MALE", "s", "נכנס");
        enter.addForm("past", "FEMALE", "s", "נכנסה");
        enter.addForm("past", "MALE", "p", "נכנסו");
        enter.addForm("past", "FEMALE", "p", "נכנסו");
        enter.addForm("inf", "ANY", "ANY", "להיכנס");
        verbs.add(enter);

        // --- מכר ---
        VerbTag sell = new VerbTag("sell");
        sell.addForm("past", "MALE", "s", "מכר");
        sell.addForm("past", "FEMALE", "s", "מכרה");
        sell.addForm("past", "MALE", "p", "מכרו");
        sell.addForm("past", "FEMALE", "p", "מכרו");
        sell.addForm("inf", "ANY", "ANY", "למכור");
        verbs.add(sell);

        // --- לקח ---
        VerbTag take = new VerbTag("take");
        take.addForm("past", "MALE", "s", "לקח");
        take.addForm("past", "FEMALE", "s", "לקחה");
        take.addForm("past", "MALE", "p", "לקחו");
        take.addForm("past", "FEMALE", "p", "לקחו");
        take.addForm("inf", "ANY", "ANY", "לקחת");
        verbs.add(take);

        // --- שם / הניח ---
        VerbTag put = new VerbTag("put");
        put.addForm("past", "MALE", "s", "שם");
        put.addForm("past", "FEMALE", "s", "שמה");
        put.addForm("past", "MALE", "p", "שמו");
        put.addForm("past", "FEMALE", "p", "שמו");
        put.addForm("inf", "ANY", "ANY", "לשים");
        verbs.add(put);

        // --- סידר ---
        VerbTag arrange = new VerbTag("arrange");
        arrange.addForm("past", "MALE", "s", "סידר");
        arrange.addForm("past", "FEMALE", "s", "סידרה");
        arrange.addForm("past", "MALE", "p", "סידרו");
        arrange.addForm("past", "FEMALE", "p", "סידרו");
        arrange.addForm("inf", "ANY", "ANY", "לסדר");
        verbs.add(arrange);

        // --- שילם ---
        VerbTag pay = new VerbTag("pay");
        pay.addForm("past", "MALE", "s", "שילם");
        pay.addForm("past", "FEMALE", "s", "שילמה");
        pay.addForm("past", "MALE", "p", "שילמו");
        pay.addForm("past", "FEMALE", "p", "שילמו");
        pay.addForm("inf", "ANY", "ANY", "לשלם");

        pay.addForm("future", "MALE", "s", "ישלם");
        pay.addForm("future", "FEMALE", "s", "תשלם");
        pay.addForm("future", "MALE", "p", "ישלמו");
        pay.addForm("future", "FEMALE", "p", "ישלמו");

        verbs.add(pay);

        // --- חסך ---
        VerbTag save = new VerbTag("save");
        save.addForm("past", "MALE", "s", "חסך");
        save.addForm("past", "FEMALE", "s", "חסכה");
        save.addForm("past", "MALE", "p", "חסכו");
        save.addForm("past", "FEMALE", "p", "חסכו");
        save.addForm("inf", "ANY", "ANY", "לחסוך");
        verbs.add(save);

        // --- סיים ---
        VerbTag finish = new VerbTag("finish");
        finish.addForm("past", "MALE", "s", "סיים");
        finish.addForm("past", "FEMALE", "s", "סיימה");
        finish.addForm("past", "MALE", "p", "סיימו");
        finish.addForm("past", "FEMALE", "p", "סיימו");
        finish.addForm("inf", "ANY", "ANY", "לסיים");
        verbs.add(finish);

        // --- פעלים מיוחדים לתבניות (רצה, צריך, הציע) ---
        VerbTag offer = new VerbTag("offer");
        offer.addForm("past", "MALE", "s", "הציע");
        offer.addForm("past", "FEMALE", "s", "הציעה");
        offer.addForm("inf", "ANY", "ANY", "להציע");
        verbs.add(offer);

        VerbTag want = new VerbTag("want");
        want.addForm("past", "MALE", "s", "רצה");
        want.addForm("past", "FEMALE", "s", "רצתה");
        verbs.add(want);

        VerbTag need = new VerbTag("need");
        need.addForm("past", "MALE", "s", "היה צריך");
        need.addForm("past", "FEMALE", "s", "הייתה צריכה");
        verbs.add(need);

        VerbTag be = new VerbTag("be");
        be.addForm("past", "MALE", "s", "היה");
        be.addForm("past", "FEMALE", "s", "הייתה");
        be.addForm("past", "MALE", "p", "היו");
        be.addForm("past", "FEMALE", "p", "היו");
        be.addForm("inf", "ANY", "ANY", "להיות");
        verbs.add(be);

        VerbTag can = new VerbTag("can");
        can.addForm("past", "MALE", "s", "יכל");
        can.addForm("past", "FEMALE", "s", "יכלה");
        can.addForm("past", "MALE", "p", "יכלו");
        can.addForm("past", "FEMALE", "p", "יכלו");

        can.addForm("present", "MALE", "s", "יכול");
        can.addForm("present", "FEMALE", "s", "יכולה");
        can.addForm("present", "MALE", "p", "יכולים");
        can.addForm("present", "FEMALE", "p", "יכולות");

        can.addForm("inf", "ANY", "ANY", "להצליח");
        verbs.add(can);

        VerbTag sit = new VerbTag("sit");
        sit.addForm("past", "MALE", "s", "ישב");
        sit.addForm("past", "FEMALE", "s", "ישבה");
        sit.addForm("past", "MALE", "p", "ישבו");
        sit.addForm("past", "FEMALE", "p", "ישבו");

        sit.addForm("inf", "ANY", "ANY", "לשבת");
        verbs.add(sit);

        // --- הוסיף ---
        VerbTag add = new VerbTag("add");

        add.addForm("past", "MALE", "s", "הוסיף");
        add.addForm("past", "FEMALE", "s", "הוסיפה");
        add.addForm("past", "MALE", "p", "הוסיפו");
        add.addForm("past", "FEMALE", "p", "הוסיפו");

        add.addForm("future", "MALE", "s", "יוסיף");
        add.addForm("future", "FEMALE", "s", "תוסיף");
        add.addForm("future", "MALE", "p", "יוסיפו");
        add.addForm("future", "FEMALE", "p", "יוסיפו");

        add.addForm("inf", "ANY", "ANY", "להוסיף");

        verbs.add(add);

        VerbTag read = new VerbTag("read");
        read.addForm("past", "MALE", "s", "קרא");
        read.addForm("past", "FEMALE", "s", "קראה");
        read.addForm("present", "MALE", "s", "קורא");
        read.addForm("present", "FEMALE", "s", "קוראת");

        read.addForm("inf", "ANY", "ANY", "לקרוא");
        verbs.add(read);

        verbs.add(add);

        VerbTag cut = new VerbTag("cut");
        cut.addForm("past", "MALE", "s", "חתך");
        cut.addForm("past", "FEMALE", "s", "חתכה");
        add.addForm("past", "MALE", "p", "חתכו");
        add.addForm("past", "FEMALE", "p", "חתכו");

        cut.addForm("inf", "ANY", "ANY", "לחתוך");
        verbs.add(cut);

        return verbs;
    }

    public static ArrayList<PlaceTag> fillPlaces() {
        ArrayList<PlaceTag> places = new ArrayList<>();
        // --- חנויות מזון ---
        places.add(new PlaceTag("supermarket", "סופרמרקט", "סופרמרקטים", Gender.MALE, PlaceType.STORE,
                ItemCategory.PRODUCE, ItemCategory.BAKED_GOODS, ItemCategory.DRINKS, ItemCategory.SWEETS, ItemCategory.GENERAL_FOOD, ItemCategory.HARDWARE));
        places.add(new PlaceTag("grocery", "מכולת", "מכולות", Gender.FEMALE, PlaceType.STORE,
                ItemCategory.PRODUCE, ItemCategory.BAKED_GOODS, ItemCategory.DRINKS, ItemCategory.SWEETS, ItemCategory.GENERAL_FOOD));
        places.add(new PlaceTag("bakery", "מאפייה", "מאפיות", Gender.FEMALE, PlaceType.STORE,
                ItemCategory.BAKED_GOODS, ItemCategory.DRINKS));
        places.add(new PlaceTag("kiosk", "קיוסק", "קיוסקים", Gender.MALE, PlaceType.STORE,
                ItemCategory.SWEETS, ItemCategory.DRINKS));
        places.add(new PlaceTag("market", "שוק", "שווקים", Gender.MALE, PlaceType.STORE,
                ItemCategory.PRODUCE, ItemCategory.BAKED_GOODS, ItemCategory.SWEETS, ItemCategory.COLLECTIBLE, ItemCategory.CLOTHING, ItemCategory.HARDWARE));

        // --- חנויות מתמחות ---
        places.add(new PlaceTag("craft_store", "חנות יצירה", "חנויות יצירה", Gender.FEMALE, PlaceType.STORE,
                ItemCategory.ART_SUPPLIES)); // מוכרת רק יצירה!
        places.add(new PlaceTag("stationery_store", "חנות כלי כתיבה", "חנויות כלי כתיבה", Gender.FEMALE, PlaceType.STORE,
                ItemCategory.STATIONERY, ItemCategory.ART_SUPPLIES)); // מוכרת כלי כתיבה וקצת יצירה
        places.add(new PlaceTag("toy_store", "חנות צעצועים", "חנויות צעצועים", Gender.FEMALE, PlaceType.STORE,
                ItemCategory.TOY));
        places.add(new PlaceTag("antique_store", "חנות עתיקות", "חנויות עתיקות", Gender.FEMALE, PlaceType.STORE,
                ItemCategory.ANTIQUE));
        places.add(new PlaceTag("clothing_store", "חנות בגדים", "חנויות בגדים", Gender.FEMALE, PlaceType.STORE,
                ItemCategory.CLOTHING));
        places.add(new PlaceTag("electronics_store", "חנות אלקטרוניקה", "חנויות אלקטרוניקה", Gender.FEMALE, PlaceType.STORE,
                ItemCategory.ELECTRONICS));
        places.add(new PlaceTag("hardware_store", "טמבורייה", "טמבוריות", Gender.FEMALE, PlaceType.STORE,
                ItemCategory.HARDWARE)); // הבית החדש של החבלים והצינורות!

        // --- בריאות ---
        places.add(new PlaceTag("pharmacy", "בית מרקחת", "בתי מרקחת", Gender.MALE, PlaceType.HEALTH,
                ItemCategory.MEDICAL));
        places.add(new PlaceTag("hospital", "בית חולים", "בתי חולים", Gender.MALE, PlaceType.HEALTH,
                ItemCategory.MEDICAL, ItemCategory.DRINKS, ItemCategory.BAKED_GOODS)); // קפיטריה בבית חולים

        // --- חינוך (מקומות שבהם משתמשים בציוד, לא קונים אותו) ---
        places.add(new PlaceTag("school", "בית ספר", "בתי ספר", Gender.MALE, PlaceType.EDUCATION,
                ItemCategory.STATIONERY, ItemCategory.ART_SUPPLIES));
        places.add(new PlaceTag("library", "ספרייה", "ספריות", Gender.FEMALE, PlaceType.EDUCATION,
                ItemCategory.STATIONERY));

        // --- פנאי ותחבורה (מקומות שבהם קונים חטיפים) ---
        places.add(new PlaceTag("cinema", "קולנוע", "בתי קולנוע", Gender.MALE, PlaceType.ENTERTAINMENT,
                ItemCategory.SWEETS, ItemCategory.DRINKS));
        places.add(new PlaceTag("airport", "שדה תעופה", "שדות תעופה", Gender.MALE, PlaceType.TRANSPORTATION,
                ItemCategory.SWEETS, ItemCategory.DRINKS, ItemCategory.ELECTRONICS, ItemCategory.CLOTHING)); // בדיוטי פרי

        // ==========================================
        // מגורים (HOME)
        // ==========================================
        places.add(new PlaceTag("house", "בית", "בתים", Gender.MALE, PlaceType.HOME, ItemCategory.GENERAL_FOOD, ItemCategory.CLOTHING, ItemCategory.ELECTRONICS, ItemCategory.TOY));
        places.add(new PlaceTag("apartment", "דירה", "דירות", Gender.FEMALE, PlaceType.HOME, ItemCategory.GENERAL_FOOD, ItemCategory.CLOTHING, ItemCategory.ELECTRONICS, ItemCategory.TOY));

        // ==========================================
        // מרחב ציבורי וטבע (PUBLIC & OUTDOORS)
        // ==========================================
        // ברחוב ובכיכר הגיוני למצוא דברים שנפלו לאנשים: כלי כתיבה, או פריטי לבוש (כמו כובע)
        places.add(new PlaceTag("street", "רחוב", "רחובות", Gender.MALE, PlaceType.PUBLIC, ItemCategory.CLOTHING, ItemCategory.STATIONERY));
        places.add(new PlaceTag("square", "כיכר", "כיכרות", Gender.FEMALE, PlaceType.PUBLIC, ItemCategory.CLOTHING, ItemCategory.STATIONERY));
        places.add(new PlaceTag("park", "פארק", "פארקים", Gender.MALE, PlaceType.OUTDOORS, ItemCategory.TOY, ItemCategory.SWEETS, ItemCategory.CLOTHING));
        places.add(new PlaceTag("forest", "יער", "יערות", Gender.MALE, PlaceType.OUTDOORS, ItemCategory.HARDWARE, ItemCategory.CLOTHING));
        places.add(new PlaceTag("beach", "חוף ים", "חופי ים", Gender.MALE, PlaceType.OUTDOORS, ItemCategory.TOY, ItemCategory.CLOTHING, ItemCategory.DRINKS));

        // --- מסעדות ומזון (FOOD_SERVICE) ---
        places.add(new PlaceTag("restaurant", "מסעדה", "מסעדות", Gender.FEMALE, PlaceType.FOOD_SERVICE, ItemCategory.PRODUCE, ItemCategory.DRINKS, ItemCategory.SWEETS, ItemCategory.BAKED_GOODS));
        places.add(new PlaceTag("cafe", "בית קפה", "בתי קפה", Gender.MALE, PlaceType.FOOD_SERVICE, ItemCategory.BAKED_GOODS, ItemCategory.DRINKS, ItemCategory.SWEETS));
        places.add(new PlaceTag("pizzeria", "פיצרייה", "פיצריות", Gender.FEMALE, PlaceType.FOOD_SERVICE, ItemCategory.BAKED_GOODS, ItemCategory.DRINKS));

// --- מרכזי קניות ---
        places.add(new PlaceTag("mall", "קניון", "קניונים", Gender.MALE, PlaceType.STORE, ItemCategory.CLOTHING, ItemCategory.ELECTRONICS, ItemCategory.TOY, ItemCategory.SWEETS, ItemCategory.DRINKS));

// --- חינוך ואקדמיה ---
        places.add(new PlaceTag("university", "אוניברסיטה", "אוניברסיטאות", Gender.FEMALE, PlaceType.EDUCATION, ItemCategory.STATIONERY, ItemCategory.ELECTRONICS));
        places.add(new PlaceTag("classroom", "כיתה", "כיתות", Gender.FEMALE, PlaceType.EDUCATION, ItemCategory.STATIONERY, ItemCategory.ART_SUPPLIES));

// --- בידור, פנאי וספורט ---
        places.add(new PlaceTag("museum", "מוזיאון", "מוזיאונים", Gender.MALE, PlaceType.ENTERTAINMENT, ItemCategory.ANTIQUE, ItemCategory.STATIONERY));
        places.add(new PlaceTag("zoo", "גן חיות", "גני חיות", Gender.MALE, PlaceType.ENTERTAINMENT, ItemCategory.SWEETS, ItemCategory.DRINKS));
        places.add(new PlaceTag("amusement_park", "פארק שעשועים", "פארקי שעשועים", Gender.MALE, PlaceType.ENTERTAINMENT, ItemCategory.SWEETS, ItemCategory.DRINKS, ItemCategory.TOY));
        places.add(new PlaceTag("pool", "בריכה", "בריכות", Gender.FEMALE, PlaceType.ENTERTAINMENT, ItemCategory.SWEETS, ItemCategory.DRINKS, ItemCategory.CLOTHING));

// --- תחבורה ---
        places.add(new PlaceTag("bus_station", "תחנת אוטובוס", "תחנות אוטובוס", Gender.FEMALE, PlaceType.TRANSPORTATION, ItemCategory.DRINKS, ItemCategory.SWEETS));
        places.add(new PlaceTag("train_station", "תחנת רכבת", "תחנות רכבת", Gender.FEMALE, PlaceType.TRANSPORTATION, ItemCategory.DRINKS, ItemCategory.SWEETS, ItemCategory.BAKED_GOODS));

// --- בריאות ---
        places.add(new PlaceTag("clinic", "מרפאה", "מרפאות", Gender.FEMALE, PlaceType.HEALTH, ItemCategory.MEDICAL));

        return places;
    }

    public static List<AdjectiveTag> fillAdjectives() {
        List<AdjectiveTag>  adjectives = new ArrayList<>();
        // --- צבעים ---
        AdjectiveTag red = new AdjectiveTag("red", AdjectiveType.COLOR);
        red.addForm("MALE", "s", "אדום");
        red.addForm("FEMALE", "s", "אדומה");
        red.addForm("MALE", "p", "אדומים");
        red.addForm("FEMALE", "p", "אדומות");
        adjectives.add(red);

        AdjectiveTag blue = new AdjectiveTag("blue", AdjectiveType.COLOR);
        blue.addForm("MALE", "s", "כחול");
        blue.addForm("FEMALE", "s", "כחולה");
        blue.addForm("MALE", "p", "כחולים");
        blue.addForm("FEMALE", "p", "כחולות");
        adjectives.add(blue);

        // --- גדלים ---
        AdjectiveTag big = new AdjectiveTag("big", AdjectiveType.SIZE);
        big.addForm("MALE", "s", "גדול");
        big.addForm("FEMALE", "s", "גדולה");
        big.addForm("MALE", "p", "גדולים");
        big.addForm("FEMALE", "p", "גדולות");
        adjectives.add(big);

        AdjectiveTag small = new AdjectiveTag("small", AdjectiveType.SIZE);
        small.addForm("MALE", "s", "קטן");
        small.addForm("FEMALE", "s", "קטנה");
        small.addForm("MALE", "p", "קטנים");
        small.addForm("FEMALE", "p", "קטנות");
        adjectives.add(small);

        // --- מצבים (CONDITION) ---
        AdjectiveTag newAdj = new AdjectiveTag("new", AdjectiveType.CONDITION);
        newAdj.addForm("MALE", "s", "חדש");
        newAdj.addForm("FEMALE", "s", "חדשה");
        newAdj.addForm("MALE", "p", "חדשים");
        newAdj.addForm("FEMALE", "p", "חדשות");
        adjectives.add(newAdj);

        AdjectiveTag oldAdj = new AdjectiveTag("old", AdjectiveType.CONDITION);
        oldAdj.addForm("MALE", "s", "ישן");
        oldAdj.addForm("FEMALE", "s", "ישנה");
        oldAdj.addForm("MALE", "p", "ישנים");
        oldAdj.addForm("FEMALE", "p", "ישנות");
        adjectives.add(oldAdj);

        // הוספה: שבור (מתאים לחומרי בניין, צעצועים, אלקטרוניקה)
        AdjectiveTag broken = new AdjectiveTag("broken", AdjectiveType.CONDITION);
        broken.addForm("MALE", "s", "שבור");
        broken.addForm("FEMALE", "s", "שבורה");
        broken.addForm("MALE", "p", "שבורים");
        broken.addForm("FEMALE", "p", "שבורות");
        adjectives.add(broken);

        // הוספה: טרי (מתאים לפירות, ירקות ומאפים)
        AdjectiveTag fresh = new AdjectiveTag("fresh", AdjectiveType.CONDITION);
        fresh.addForm("MALE", "s", "טרי");
        fresh.addForm("FEMALE", "s", "טרייה");
        fresh.addForm("MALE", "p", "טריים");
        fresh.addForm("FEMALE", "p", "טריות");
        adjectives.add(fresh);

        // --- תחושות/טעם (FEELING) ---
        AdjectiveTag tasty = new AdjectiveTag("tasty", AdjectiveType.FEELING);
        tasty.addForm("MALE", "s", "טעים");
        tasty.addForm("FEMALE", "s", "טעימה");
        tasty.addForm("MALE", "p", "טעימים");
        tasty.addForm("FEMALE", "p", "טעימות");
        adjectives.add(tasty);

        return  adjectives;
    }

    public static List<UnitTag> fillUnits() {
        List<UnitTag> units = new ArrayList<>();
        // משקל ואורך
        units.add(new UnitTag("kg", "קילוגרם", "קילוגרמים", Gender.MALE, UnitType.WEIGHT, ItemCategory.PRODUCE, ItemCategory.GENERAL_FOOD, ItemCategory.ART_SUPPLIES));
        units.add(new UnitTag("meter", "מטר", "מטרים", Gender.MALE, UnitType.LENGTH, ItemCategory.HARDWARE));
        units.add(new UnitTag("roll", "גליל", "גלילים", Gender.MALE, UnitType.LENGTH, ItemCategory.HARDWARE));

        // נפח
        units.add(new UnitTag("liter", "ליטר", "ליטרים", Gender.MALE, UnitType.VOLUME, ItemCategory.DRINKS));
        units.add(new UnitTag("bottle", "בקבוק", "בקבוקים", Gender.MALE, UnitType.VOLUME, ItemCategory.DRINKS));

        // ספירה מיוחדת
        units.add(new UnitTag("box", "מארז", "מארזים", Gender.MALE, UnitType.COUNT, ItemCategory.SWEETS, ItemCategory.TOY, ItemCategory.STATIONERY, ItemCategory.ELECTRONICS, ItemCategory.MEDICAL, ItemCategory.ART_SUPPLIES));
        units.add(new UnitTag("pack", "חבילה", "חבילות", Gender.FEMALE, UnitType.COUNT, ItemCategory.SWEETS, ItemCategory.STATIONERY, ItemCategory.COLLECTIBLE, ItemCategory.MEDICAL, ItemCategory.ART_SUPPLIES));
        units.add(new UnitTag("tray", "מגש", "מגשים", Gender.MALE, UnitType.COUNT, ItemCategory.BAKED_GOODS)); // רק מאפים!

        // כלים לסחורה כבדה, חקלאות ועתיקות
        units.add(new UnitTag("sack", "שק", "שקים", Gender.MALE, UnitType.COUNT,
                ItemCategory.COLLECTIBLE, ItemCategory.PRODUCE, ItemCategory.GENERAL_FOOD));
        // נמחק ANTIQUE! מטבעות זהב ייכנסו לפה בזכות COLLECTIBLE.

        units.add(new UnitTag("crate", "ארגז", "ארגזים", Gender.MALE, UnitType.COUNT,
                ItemCategory.ANTIQUE, ItemCategory.PRODUCE, ItemCategory.DRINKS, ItemCategory.TOY, ItemCategory.HARDWARE));
        // נשאר ANTIQUE! כדי חרס ייכנסו אך ורק לפה.

        units.add(new UnitTag("chest", "תיבה", "תיבות", Gender.FEMALE, UnitType.COUNT,
                ItemCategory.COLLECTIBLE));
        // נמחק ANTIQUE!

        // היחידה הריקה (כדי שלא יתווסף "יחידות של" לכל דבר)
        units.add(new UnitTag("none_unit", "", "", Gender.MALE, UnitType.NONE,
                ItemCategory.ELECTRONICS, ItemCategory.CLOTHING, ItemCategory.TOY, ItemCategory.PRODUCE, ItemCategory.BAKED_GOODS, ItemCategory.SWEETS, ItemCategory.STATIONERY, ItemCategory.COLLECTIBLE, ItemCategory.ANTIQUE, ItemCategory.MEDICAL, ItemCategory.ART_SUPPLIES, ItemCategory.HARDWARE));

        return units;
    }

    public static List<RoleTag> fillRoles() {
        // ==========================================
        // 1. מפעילים (OPERATORS) - נותני השירות
        // ==========================================
        List<RoleTag> roles = new ArrayList<>();

        // -- חנויות ועסקים --
        roles.add(new RoleTag("seller", "מוכר", "מוכרים", "מוכרת", "מוכרות", RoleType.OPERATOR,
                "grocery", "kiosk", "toy_store", "craft_store", "stationery_store", "clothing_store", "electronics_store", "hardware_store"));

        roles.add(new RoleTag("cashier", "קופאי", "קופאים", "קופאית", "קופאיות", RoleType.OPERATOR,
                "supermarket", "grocery"));

        roles.add(new RoleTag("baker", "אופה", "אופים", "אופה", "אופות", RoleType.OPERATOR,
                "bakery"));

        roles.add(new RoleTag("merchant", "סוחר", "סוחרים", "סוחרת", "סוחרות", RoleType.OPERATOR,
                "market", "antique_store"));

        roles.add(new RoleTag("pharmacist", "רוקח", "רוקחים", "רוקחת", "רוקחות", RoleType.OPERATOR,
                "pharmacy"));

        // -- חינוך ולימודים --
        roles.add(new RoleTag("teacher", "מורה", "מורים", "מורה", "מורות", RoleType.OPERATOR,
                "school", "classroom"));

        roles.add(new RoleTag("librarian", "ספרן", "ספרנים", "ספרנית", "ספרניות", RoleType.OPERATOR,
                "library"));

        roles.add(new RoleTag("professor", "מרצה", "מרצים", "מרצה", "מרצות", RoleType.OPERATOR,
                "university"));

        // -- פנאי, בידור וטבע --
        roles.add(new RoleTag("guide", "מדריך", "מדריכים", "מדריכה", "מדריכות", RoleType.OPERATOR,
                "museum", "zoo", "park", "forest"));

        roles.add(new RoleTag("usher", "סדרן", "סדרנים", "סדרנית", "סדרניות", RoleType.OPERATOR,
                "cinema", "amusement_park"));

        roles.add(new RoleTag("lifeguard", "מציל", "מצילים", "מצילה", "מצילות", RoleType.OPERATOR,
                "pool", "beach"));

        // -- מסעדות ומזון --
        roles.add(new RoleTag("waiter", "מלצר", "מלצרים", "מלצרית", "מלצריות", RoleType.OPERATOR,
                "restaurant", "cafe", "pizzeria"));

        // -- תחבורה ונסיעות --
        roles.add(new RoleTag("driver", "נהג", "נהגים", "נהגת", "נהגות", RoleType.OPERATOR,
                "bus_station", "train_station"));

        roles.add(new RoleTag("flight_attendant", "דייל", "דיילים", "דיילת", "דיילות", RoleType.OPERATOR,
                "airport"));

        // -- ספורט ובריאות --
        roles.add(new RoleTag("doctor", "רופא", "רופאים", "רופאה", "רופאות", RoleType.OPERATOR,
                "hospital", "clinic"));

        roles.add(new RoleTag("nurse", "אח", "אחים", "אחות", "אחיות", RoleType.OPERATOR,
                "hospital", "clinic"));

        roles.add(new RoleTag("host", "מארח", "מארחים", "מארחת", "מארחות", RoleType.OPERATOR, "house", "apartment"));



        // ==========================================
        // 2. מופעלים (TARGETS) - קהל היעד / מקבלי השירות
        // ==========================================

        // -- לקוחות קלאסיים --
        roles.add(new RoleTag("customer", "לקוח", "לקוחות", "לקוחה", "לקוחות", RoleType.TARGET,
                "grocery", "supermarket", "bakery", "kiosk", "toy_store", "craft_store", "stationery_store",
                "mall", "clothing_store", "electronics_store", "pharmacy", "hardware_store"));

        roles.add(new RoleTag("buyer", "קונה", "קונים", "קונה", "קונות", RoleType.TARGET,
                "market", "antique_store"));

        // -- חינוך ולימודים --
        roles.add(new RoleTag("student", "תלמיד", "תלמידים", "תלמידה", "תלמידות", RoleType.TARGET,
                "school", "classroom", "stationery_store", "craft_store"));

        roles.add(new RoleTag("university_student", "סטודנט", "סטודנטים", "סטודנטית", "סטודנטיות", RoleType.TARGET,
                "university"));

        roles.add(new RoleTag("reader", "קורא", "קוראים", "קוראת", "קוראות", RoleType.TARGET,
                "library"));

        // -- פנאי ובידור --
        roles.add(new RoleTag("visitor", "מבקר", "מבקרים", "מבקרת", "מבקרות", RoleType.TARGET,
                "mall", "market", "museum", "zoo", "amusement_park", "park"));

        roles.add(new RoleTag("viewer", "צופה", "צופים", "צופה", "צופות", RoleType.TARGET,
                "cinema"));

        roles.add(new RoleTag("collector", "אספן", "אספנים", "אספנית", "אספניות", RoleType.TARGET,
                "antique_store", "market", "toy_store"));

        roles.add(new RoleTag("traveler", "מטייל", "מטיילים", "מטיילת", "מטיילות", RoleType.TARGET,
                "forest", "park", "street", "square"));

        roles.add(new RoleTag("bather", "מתרחץ", "מתרחצים", "מתרחצת", "מתרחצות", RoleType.TARGET,
                "pool", "beach"));

        // -- מזון, בריאות ותחבורה --
        roles.add(new RoleTag("diner", "סועד", "סועדים", "סועדת", "סועדות", RoleType.TARGET,
                "restaurant", "pizzeria", "cafe"));

        roles.add(new RoleTag("patient", "מטופל", "מטופלים", "מטופלת", "מטופלות", RoleType.TARGET,
                "hospital", "clinic", "pharmacy"));

        roles.add(new RoleTag("passenger", "נוסע", "נוסעים", "נוסעת", "נוסעות", RoleType.TARGET,
                "train_station", "bus_station", "airport"));

        roles.add(new RoleTag("pedestrian", "הולך רגל", "הולכי רגל", "הולכת רגל", "הולכות רגל", RoleType.TARGET,
                "street", "square"));

        roles.add(new RoleTag("guest", "אורח", "אורחים", "אורחת", "אורחות", RoleType.TARGET, "house", "apartment"));

        return roles;
    }







    public MathQuestion generateForPlayer(RacePlayer player) {
        String expression = "המלך ביקש מעידן שיקנה לו 3 תפוחים, עידן קנה 3 תפוחים והביא מהבית עוד 2 ונתן הכל למלך. כמה תפוחים סהכ הביא עידן למלך ?";
        List<String> options = List.of("6","3","5","2");
        String correctAnswer = "5";
        int timeLimitSeconds = 15;
        int score = 20;

        return new MathQuestion("",expression,options,"",correctAnswer,timeLimitSeconds,score);
    }
}
