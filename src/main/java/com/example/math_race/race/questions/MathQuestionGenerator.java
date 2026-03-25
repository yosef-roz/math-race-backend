package com.example.math_race.race.questions;

import com.example.math_race.race.RacePlayer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.math_race.race.questions.AdjectiveType.*;
import static com.example.math_race.race.questions.ItemCategory.*;

@Component
public class MathQuestionGenerator {


    static ArrayList<Human> humans =  new ArrayList<>();
    static ArrayList<Item> items =  new ArrayList<>();
    static ArrayList<Verb> verbs = new ArrayList<>();
    static ArrayList<Place> places = new ArrayList<>();
    public static List<Adjective> adjectives = new ArrayList<>();
    public static List<Unit> units = new ArrayList<>();
    private static List<RoleEntity> roles = new ArrayList<>();

    static {
        fillHumans();
        fillVerbs();
        fillItems();
        fillPlaces();
        fillAdjectives();
        fillUnits();
        fillRoles();
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

                    // מפה רגיל

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
                    else if (conditionFromTag.contains(">")) operator = ">";
                    else if (conditionFromTag.contains("<")) operator = "<";
                    else if (conditionFromTag.contains("=")) operator = "=";

                    if (!operator.isEmpty()) {
                        int opIndex = conditionFromTag.indexOf(operator);
                        String leftSide = conditionFromTag.substring(0, opIndex).trim();
                        String expectedStr = conditionFromTag.substring(opIndex + operator.length()).trim();

                        String entityId = leftSide;
                        String propertyKey = "";

                        if (leftSide.contains(":")) {
                            String[] varParts = leftSide.split(":", 2);
                            entityId = varParts[0].trim();
                            propertyKey = varParts[1].trim();
                        }

                        String actualStr = "";
                        if (memory.containsKey(entityId)) {
                            actualStr = memory.get(entityId).getProperty(propertyKey);
                        }

                        boolean conditionMet = false;
                        if (operator.equals("=")) {
                            conditionMet = actualStr.equals(expectedStr);
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
                                System.out.println("Warning: Numeric comparison failed for: " + actualStr);
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


    public static Human findHuman(Map<String, String> constraints) {
        List<Human> matches = humans.stream()
                .filter(h -> h.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No human matches constraints: " + constraints);
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static QuestionEntity findAdjective(Map<String, String> constraints) {
        List<Adjective> matches = adjectives.stream()
                .filter(a -> a.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No adjective matches constraints: " + constraints);
            return null;
        }

        Adjective chosenAdjective = matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));

        String g = constraints.getOrDefault("g", "MALE").toUpperCase();
        String num = constraints.getOrDefault("num", "s").toLowerCase();

        String exactWord = chosenAdjective.getWord(g, num);

        return key -> exactWord;
    }

    public static Item findItem(Map<String, String> constraints) {
        List<Item> matches = items.stream()
                .filter(h -> h.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No item matches constraints: " + constraints);
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static Place findPlace(Map<String, String> constraints) {
        List<Place> matches = places.stream()
                .filter(p -> p.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No place matches constraints: " + constraints); // שונה ל-place
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static NumberEntity findNumber(Map<String, String> constraints) {
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
                        return new NumberEntity(min);
                    }

                    int randomNumber;
                    do {
                        randomNumber = java.util.concurrent.ThreadLocalRandom.current().nextInt(min, max + 1);
                    } while (randomNumber == forbiddenValue);

                    return new NumberEntity(randomNumber);
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Invalid value: " + valStr);
                }
            }

            else {
                try {
                    return new NumberEntity(Integer.parseInt(valStr));
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Invalid value for number: " + valStr);
                }
            }
        }

        int randomNumber = java.util.concurrent.ThreadLocalRandom.current().nextInt(min, max + 1);
        return new NumberEntity(randomNumber);
    }

    public static TimeEntity findTime(Map<String, String> constraints) {
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
                        return new TimeEntity(minMinutes);
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

                    return new TimeEntity(randomMinutes);

                } catch (Exception e) {
                    System.out.println("Warning: Invalid time format for forbidden value: " + valStr);
                }
            }
            else {
                try {
                    return new TimeEntity(parseTime(valStr));
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

        return new TimeEntity(randomMinutes);
    }

    private static int parseTime(String timeStr) {
        String[] parts = timeStr.trim().split("[:.]");
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        return h * 60 + m;
    }

    public static QuestionEntity findVerb(Map<String, String> constraints) {
        List<Verb> matches = verbs.stream()
                .filter(v -> v.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No verb matches constraints: " + constraints);
            return null;
        }

        Verb chosenVerb = matches.get(ThreadLocalRandom.current().nextInt(matches.size()));

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
        List<Unit> matches = units.stream()
                .filter(u -> u.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No unit matches constraints: " + constraints);
            return null;
        }

        return matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static QuestionEntity findRole(Map<String, String> constraints) {
        List<RoleEntity> matches = roles.stream() // בהנחה שיש לך רשימה סטטית של תפקידים שנקראת roles
                .filter(r -> r.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No role matches constraints: " + constraints);
            return null;
        }

        // בחירה אקראית מתוך התפקידים שעברו את הסינון
        return matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static void fillHumans() {
        // --- בנים (25) ---
        humans.add(new Human("שמעון", Gender.MALE));
        humans.add(new Human("יוסף", Gender.MALE));
        humans.add(new Human("אברהם", Gender.MALE));
        humans.add(new Human("דוד", Gender.MALE));
        humans.add(new Human("משה", Gender.MALE));
        humans.add(new Human("אריאל", Gender.MALE));
        humans.add(new Human("נועם", Gender.MALE));
        humans.add(new Human("איתי", Gender.MALE));
        humans.add(new Human("אורי", Gender.MALE));
        humans.add(new Human("עומר", Gender.MALE));
        humans.add(new Human("דניאל", Gender.MALE));
        humans.add(new Human("יהונתן", Gender.MALE));
        humans.add(new Human("רועי", Gender.MALE));
        humans.add(new Human("עידן", Gender.MALE));
        humans.add(new Human("עמית", Gender.MALE));
        humans.add(new Human("גיא", Gender.MALE));
        humans.add(new Human("מאור", Gender.MALE));
        humans.add(new Human("תומר", Gender.MALE));
        humans.add(new Human("אלעד", Gender.MALE));
        humans.add(new Human("ירון", Gender.MALE));
        humans.add(new Human("אורן", Gender.MALE));
        humans.add(new Human("ברק", Gender.MALE));
        humans.add(new Human("גלעד", Gender.MALE));
        humans.add(new Human("ניר", Gender.MALE));
        humans.add(new Human("אסף", Gender.MALE));

        // --- בנות (25) ---
        humans.add(new Human("נועה", Gender.FEMALE));
        humans.add(new Human("תמר", Gender.FEMALE));
        humans.add(new Human("יעל", Gender.FEMALE));
        humans.add(new Human("מאיה", Gender.FEMALE));
        humans.add(new Human("אביגיל", Gender.FEMALE));
        humans.add(new Human("טליה", Gender.FEMALE));
        humans.add(new Human("עדי", Gender.FEMALE));
        humans.add(new Human("שירה", Gender.FEMALE));
        humans.add(new Human("מיכל", Gender.FEMALE));
        humans.add(new Human("רוני", Gender.FEMALE));
        humans.add(new Human("אלה", Gender.FEMALE));
        humans.add(new Human("עדן", Gender.FEMALE));
        humans.add(new Human("הילה", Gender.FEMALE));
        humans.add(new Human("דנה", Gender.FEMALE));
        humans.add(new Human("מורן", Gender.FEMALE));
        humans.add(new Human("קרן", Gender.FEMALE));
        humans.add(new Human("ענת", Gender.FEMALE));
        humans.add(new Human("מירי", Gender.FEMALE));
        humans.add(new Human("גלי", Gender.FEMALE));
        humans.add(new Human("סיון", Gender.FEMALE));
        humans.add(new Human("רותם", Gender.FEMALE)); // מתאים גם לבנים, אבל הגדרנו כבת
        humans.add(new Human("שיר", Gender.FEMALE));
        humans.add(new Human("אור", Gender.FEMALE));   // כנ"ל
        humans.add(new Human("שחר", Gender.FEMALE));   // כנ"ל
        humans.add(new Human("ענבל", Gender.FEMALE));
    }

    public static void fillItems() {
        // הגדרת קבוצות יחידות מראש לשימוש חוזר
        Set<UnitType> weightOnly = Set.of(UnitType.WEIGHT);
        Set<UnitType> volumeOnly = Set.of(UnitType.VOLUME);
        Set<UnitType> countOnly = Set.of(UnitType.COUNT);
        Set<UnitType> lengthOnly = Set.of(UnitType.LENGTH);
        Set<UnitType> weightOrCount = Set.of(UnitType.WEIGHT, UnitType.COUNT);
        Set<UnitType> volumeOrCount = Set.of(UnitType.VOLUME, UnitType.COUNT);
        Set<UnitType> noUnit = Set.of(UnitType.NONE);

        // --- FOOD (אוכל) ---
        items.add(new Item("תפוח", "תפוחים", Gender.MALE, weightOrCount, ItemCategory.FOOD, ItemCategory.GENERAL));
        items.add(new Item("בננה", "בננות", Gender.FEMALE, weightOrCount, ItemCategory.FOOD, ItemCategory.GENERAL));
        items.add(new Item("תפוז", "תפוזים", Gender.MALE, weightOrCount, ItemCategory.FOOD, ItemCategory.GENERAL));
        items.add(new Item("עגבנייה", "עגבניות", Gender.FEMALE, weightOrCount, ItemCategory.FOOD));
        items.add(new Item("מלפפון", "מלפפונים", Gender.MALE, weightOrCount, ItemCategory.FOOD));
        items.add(new Item("פיצה", "פיצות", Gender.FEMALE, countOnly, ItemCategory.FOOD));
        items.add(new Item("לחם", "כיכרות לחם", Gender.MALE, countOnly, ItemCategory.FOOD));
        items.add(new Item("קמח", "שקי קמח", Gender.MALE, weightOnly, ItemCategory.FOOD)); // קמח נמדד בעיקר במשקל (קילו)
        items.add(new Item("מים", "בקבוקי מים", Gender.MALE, volumeOrCount, ItemCategory.FOOD)); // ליטר או בקבוקים
        items.add(new Item("חלב", "קרטוני חלב", Gender.MALE, volumeOrCount, ItemCategory.FOOD));

        // --- COLLECTIBLE (פריטי אספנות מודרניים / תחביבים) ---
        items.add(new Item("קלף פוקימון", "קלפי פוקימון", Gender.MALE, countOnly, ItemCategory.COLLECTIBLE));
        items.add(new Item("בול", "בולים", Gender.MALE, countOnly, ItemCategory.COLLECTIBLE));
        items.add(new Item("חוברת קומיקס", "חוברות קומיקס", Gender.FEMALE, countOnly, ItemCategory.COLLECTIBLE));
        items.add(new Item("בובת פופ", "בובות פופ", Gender.FEMALE, countOnly, ItemCategory.COLLECTIBLE));
        items.add(new Item("מדליה", "מדליות", Gender.FEMALE, countOnly, ItemCategory.COLLECTIBLE));

        // --- ANTIQUE (עתיקות ופריטים היסטוריים - כולם גם פריטי אספנות!) ---
        items.add(new Item("מטבע זהב", "מטבעות זהב", Gender.MALE, weightOrCount, ItemCategory.ANTIQUE, ItemCategory.COLLECTIBLE, ItemCategory.MONEY));
        items.add(new Item("כד חרס", "כדי חרס", Gender.MALE, countOnly, ItemCategory.ANTIQUE, ItemCategory.COLLECTIBLE));
        items.add(new Item("פסלון ברונזה", "פסלוני ברונזה", Gender.MALE, weightOrCount, ItemCategory.ANTIQUE, ItemCategory.COLLECTIBLE));
        items.add(new Item("שעון קיר עתיק", "שעוני קיר עתיקים", Gender.MALE, countOnly, ItemCategory.ANTIQUE, ItemCategory.COLLECTIBLE));
        items.add(new Item("תיבת עץ", "תיבות עץ", Gender.FEMALE, countOnly, ItemCategory.ANTIQUE, ItemCategory.COLLECTIBLE));
        items.add(new Item("חרב עתיקה", "חרבות עתיקות", Gender.FEMALE, countOnly, ItemCategory.ANTIQUE, ItemCategory.COLLECTIBLE));

        // --- STATIONERY (ציוד משרדי ולימודי) ---
        items.add(new Item("עיפרון", "עפרונות", Gender.MALE, countOnly, ItemCategory.STATIONERY, ItemCategory.GENERAL));
        items.add(new Item("מחברת", "מחברות", Gender.FEMALE, countOnly, ItemCategory.STATIONERY, ItemCategory.GENERAL));
        items.add(new Item("מחק", "מחקים", Gender.MALE, countOnly, ItemCategory.STATIONERY));
        items.add(new Item("סרגל", "סרגלים", Gender.MALE, countOnly, ItemCategory.STATIONERY));
        items.add(new Item("עט", "עטים", Gender.MALE, countOnly, ItemCategory.STATIONERY));
        items.add(new Item("תיק", "תיקים", Gender.MALE, countOnly, ItemCategory.STATIONERY, ItemCategory.GENERAL));

        // --- TOY (צעצועים) ---
        items.add(new Item("בובה", "בובות", Gender.FEMALE, countOnly, ItemCategory.TOY));
        items.add(new Item("פאזל", "פאזלים", Gender.MALE, countOnly, ItemCategory.TOY));
        items.add(new Item("מכונית על שלט", "מכוניות על שלט", Gender.FEMALE, countOnly, ItemCategory.TOY));
        items.add(new Item("משחק קופסה", "משחקי קופסה", Gender.MALE, countOnly, ItemCategory.TOY));
        items.add(new Item("רובה מים", "רובי מים", Gender.MALE, countOnly, ItemCategory.TOY));

        // --- CLOTHING (ביגוד והנעלה - תוספת מומלצת) ---
        items.add(new Item("חולצה", "חולצות", Gender.FEMALE, noUnit, ItemCategory.CLOTHING, ItemCategory.GENERAL));
        items.add(new Item("מכנס", "מכנסיים", Gender.MALE, countOnly, ItemCategory.CLOTHING, ItemCategory.GENERAL)); // מכנסיים זה תמיד רבים, אבל לצורך המנוע "מכנס" כיחיד זה שימושי
        items.add(new Item("כובע", "כובעים", Gender.MALE, noUnit, ItemCategory.CLOTHING));
        items.add(new Item("זוג נעליים", "זוגות נעליים", Gender.MALE, noUnit, ItemCategory.CLOTHING));
        items.add(new Item("מעיל", "מעילים", Gender.MALE, noUnit, ItemCategory.CLOTHING));

        // --- ELECTRONICS (אלקטרוניקה - תוספת מומלצת) ---
        items.add(new Item("מחשב נייד", "מחשבים ניידים", Gender.MALE, countOnly, ItemCategory.ELECTRONICS));
        items.add(new Item("טלפון חכם", "טלפונים חכמים", Gender.MALE, countOnly, ItemCategory.ELECTRONICS));
        items.add(new Item("אוזנייה", "אוזניות", Gender.FEMALE, countOnly, ItemCategory.ELECTRONICS));
        items.add(new Item("מסך", "מסכים", Gender.MALE, countOnly, ItemCategory.ELECTRONICS));

        // --- GENERAL / MATERIALS (חומרים שנמדדים באורך, למשל שאלות על בנייה) ---
        items.add(new Item("כבל חשמל", "כבלי חשמל", Gender.MALE, lengthOnly, ItemCategory.ELECTRONICS, ItemCategory.GENERAL));
        items.add(new Item("חבל", "חבלים", Gender.MALE, lengthOnly, ItemCategory.GENERAL));
        items.add(new Item("צינור", "צינורות", Gender.MALE, lengthOnly, ItemCategory.GENERAL));
    }



    public static void fillVerbs() {
        // --- קנה (מתאים ל: MONEY, GENERAL) ---
        Verb buy = new Verb("buy");
        buy.addForm("past", "MALE", "s", "קנה");
        buy.addForm("past", "FEMALE", "s", "קנתה");
        buy.addForm("past", "MALE", "p", "קנו");
        buy.addForm("past", "FEMALE", "p", "קנו");
        buy.addForm("inf", "ANY", "ANY", "לקנות");
        verbs.add(buy);

        // --- אכל (מתאים ל: FOOD) ---
        Verb eat = new Verb("eat");
        eat.addForm("past", "MALE", "s", "אכל");
        eat.addForm("past", "FEMALE", "s", "אכלה");
        eat.addForm("past", "MALE", "p", "אכלו");
        eat.addForm("past", "FEMALE", "p", "אכלו");
        eat.addForm("inf", "ANY", "ANY", "לאכול");
        verbs.add(eat);

        // --- נתן (מתאים ל: COLLECTIBLE, GENERAL) ---
        Verb give = new Verb("give");
        give.addForm("past", "MALE", "s", "נתן");
        give.addForm("past", "FEMALE", "s", "נתנה");
        give.addForm("past", "MALE", "p", "נתנו");
        give.addForm("past", "FEMALE", "p", "נתנו");
        give.addForm("inf", "ANY", "ANY", "לתת");
        verbs.add(give);

        // --- קיבל (מתאים ל: COLLECTIBLE, GENERAL, MONEY) ---
        Verb receive = new Verb("receive");
        receive.addForm("past", "MALE", "s", "קיבל");
        receive.addForm("past", "FEMALE", "s", "קיבלה");
        receive.addForm("past", "MALE", "p", "קיבלו");
        receive.addForm("past", "FEMALE", "p", "קיבלו");
        receive.addForm("inf", "ANY", "ANY", "לקבל");
        verbs.add(receive);

        // --- מצא (מתאים ל: COLLECTIBLE, GENERAL) ---
        Verb find = new Verb("find");
        find.addForm("past", "MALE", "s", "מצא");
        find.addForm("past", "FEMALE", "s", "מצאה");
        find.addForm("past", "MALE", "p", "מצאו");
        find.addForm("past", "FEMALE", "p", "מצאו");
        find.addForm("inf", "ANY", "ANY", "למצוא");
        verbs.add(find);

        // --- איבד (מתאים ל: COLLECTIBLE, GENERAL) ---
        Verb lose = new Verb("lose");
        lose.addForm("past", "MALE", "s", "איבד");
        lose.addForm("past", "FEMALE", "s", "איבדה");
        lose.addForm("past", "MALE", "p", "איבדו");
        lose.addForm("past", "FEMALE", "p", "איבדו");
        lose.addForm("inf", "ANY", "ANY", "לאבד");
        verbs.add(lose);

        // --- אסף (מתאים ל: COLLECTIBLE) ---
        Verb collect = new Verb("collect");
        collect.addForm("past", "MALE", "s", "אסף");
        collect.addForm("past", "FEMALE", "s", "אספה");
        collect.addForm("past", "MALE", "p", "אספו");
        collect.addForm("past", "FEMALE", "p", "אספו");
        collect.addForm("inf", "ANY", "ANY", "לאסוף");
        verbs.add(collect);

        // --- חילק (מתאים ל: שאלות חילוק - FOOD, COLLECTIBLE) ---
        Verb divide = new Verb("divide");
        divide.addForm("past", "MALE", "s", "חילק");
        divide.addForm("past", "FEMALE", "s", "חילקה");
        divide.addForm("past", "MALE", "p", "חילקו");
        divide.addForm("past", "FEMALE", "p", "חילקו");
        divide.addForm("inf", "ANY", "ANY", "לחלק");
        verbs.add(divide);

        // --- נכנס (מתאים ל: PLACE) ---
        Verb enter = new Verb("enter");
        enter.addForm("past", "MALE", "s", "נכנס");
        enter.addForm("past", "FEMALE", "s", "נכנסה");
        enter.addForm("past", "MALE", "p", "נכנסו");
        enter.addForm("past", "FEMALE", "p", "נכנסו");
        enter.addForm("inf", "ANY", "ANY", "להיכנס");
        verbs.add(enter);

        // --- היה (פועל עזר) ---
        Verb be = new Verb("be");
        be.addForm("past", "MALE", "s", "היה");
        be.addForm("past", "FEMALE", "s", "הייתה");
        be.addForm("past", "MALE", "p", "היו");
        be.addForm("past", "FEMALE", "p", "היו");
        be.addForm("inf", "ANY", "ANY", "להיות");
        verbs.add(be);

        // ==========================================
        //            פעלים חדשים שנוספו
        // ==========================================

        // --- מכר (מתאים ל: חנויות, כסף - שאלות של חיסור מהמלאי או הוספת כסף) ---
        Verb sell = new Verb("sell");
        sell.addForm("past", "MALE", "s", "מכר");
        sell.addForm("past", "FEMALE", "s", "מכרה");
        sell.addForm("past", "MALE", "p", "מכרו");
        sell.addForm("past", "FEMALE", "p", "מכרו");
        sell.addForm("inf", "ANY", "ANY", "למכור");
        verbs.add(sell);

        // --- לקח (מתאים לחיסור: מישהו לקח משהו) ---
        Verb take = new Verb("take");
        take.addForm("past", "MALE", "s", "לקח");
        take.addForm("past", "FEMALE", "s", "לקחה");
        take.addForm("past", "MALE", "p", "לקחו");
        take.addForm("past", "FEMALE", "p", "לקחו");
        take.addForm("inf", "ANY", "ANY", "לקחת");
        verbs.add(take);

        // --- שם / הניח (מתאים לחיבור: לשים דברים בקופסה, על מדף) ---
        Verb put = new Verb("put");
        put.addForm("past", "MALE", "s", "שם");
        put.addForm("past", "FEMALE", "s", "שמה");
        put.addForm("past", "MALE", "p", "שמו");
        put.addForm("past", "FEMALE", "p", "שמו");
        put.addForm("inf", "ANY", "ANY", "לשים");
        verbs.add(put);

        // --- סידר (מתאים לכפל: סידר דברים בשורות או במדפים) ---
        Verb arrange = new Verb("arrange");
        arrange.addForm("past", "MALE", "s", "סידר");
        arrange.addForm("past", "FEMALE", "s", "סידרה");
        arrange.addForm("past", "MALE", "p", "סידרו");
        arrange.addForm("past", "FEMALE", "p", "סידרו");
        arrange.addForm("inf", "ANY", "ANY", "לסדר");
        verbs.add(arrange);

        // --- שילם (מתאים לשאלות של קניות ועודף) ---
        Verb pay = new Verb("pay");
        pay.addForm("past", "MALE", "s", "שילם");
        pay.addForm("past", "FEMALE", "s", "שילמה");
        pay.addForm("past", "MALE", "p", "שילמו");
        pay.addForm("past", "FEMALE", "p", "שילמו");
        pay.addForm("inf", "ANY", "ANY", "לשלם");
        verbs.add(pay);

        // --- חסך (מתאים לשאלות של איסוף כסף לאורך זמן) ---
        Verb save = new Verb("save");
        save.addForm("past", "MALE", "s", "חסך");
        save.addForm("past", "FEMALE", "s", "חסכה");
        save.addForm("past", "MALE", "p", "חסכו");
        save.addForm("past", "FEMALE", "p", "חסכו");
        save.addForm("inf", "ANY", "ANY", "לחסוך");
        verbs.add(save);

        // --- סיים ---
        Verb finish = new Verb("finish");
        finish.addForm("past", "MALE", "s", "סיים");
        finish.addForm("past", "FEMALE", "s", "סיימה");
        finish.addForm("past", "MALE", "p", "סיימו");
        finish.addForm("past", "FEMALE", "p", "סיימו");
        finish.addForm("inf", "ANY", "ANY", "לסיים");
        verbs.add(finish);

        Verb offer = new Verb("offer");
        offer.addForm("past", "MALE", "s", "הציע");
        offer.addForm("past", "FEMALE", "s", "הציעה");
        offer.addForm("inf", "ANY", "ANY", "להציע");
        verbs.add(offer);

        Verb want = new Verb("want");
        want.addForm("past", "MALE", "s", "רצה");
        want.addForm("past", "FEMALE", "s", "רצתה");
        verbs.add(want);

        Verb need = new Verb("need");
        need.addForm("past", "MALE", "s", "היה צריך");
        need.addForm("past", "FEMALE", "s", "הייתה צריכה");
        verbs.add(need);
    }

    public static void fillPlaces() {
        // ==========================================
        // חנויות ועסקים (STORE)
        // ==========================================
        places.add(new Place("grocery", "מכולת", "מכולות", Gender.FEMALE, PlaceType.STORE, FOOD, GENERAL));
        places.add(new Place("supermarket", "סופרמרקט", "סופרמרקטים", Gender.MALE, PlaceType.STORE, FOOD, GENERAL));
        places.add(new Place("bakery", "מאפייה", "מאפיות", Gender.FEMALE, PlaceType.STORE, FOOD));
        places.add(new Place("kiosk", "קיוסק", "קיוסקים", Gender.MALE, PlaceType.STORE, FOOD));
        places.add(new Place("market", "שוק", "שווקים", Gender.MALE, PlaceType.STORE, FOOD, COLLECTIBLE, GENERAL, CLOTHING));
        places.add(new Place("toy_store", "חנות צעצועים", "חנויות צעצועים", Gender.FEMALE, PlaceType.STORE, TOY));
        places.add(new Place("antique_store", "חנות עתיקות", "חנויות עתיקות", Gender.FEMALE, PlaceType.STORE, ANTIQUE));
        places.add(new Place("craft_store", "חנות יצירה", "חנויות יצירה", Gender.FEMALE, PlaceType.STORE, STATIONERY, TOY));
        places.add(new Place("stationery_store", "חנות כלי כתיבה", "חנויות כלי כתיבה", Gender.FEMALE, PlaceType.STORE, STATIONERY, GENERAL));
        places.add(new Place("mall", "קניון", "קניונים", Gender.MALE, PlaceType.STORE, GENERAL, FOOD, TOY, STATIONERY, MONEY, CLOTHING, ELECTRONICS));
        places.add(new Place("clothing_store", "חנות בגדים", "חנויות בגדים", Gender.FEMALE, PlaceType.STORE, CLOTHING));
        places.add(new Place("electronics_store", "חנות אלקטרוניקה", "חנויות אלקטרוניקה", Gender.FEMALE, PlaceType.STORE, ELECTRONICS));

        // ==========================================
        // מגורים (HOME)
        // ==========================================
        places.add(new Place("house", "בית", "בתים", Gender.MALE, PlaceType.HOME, GENERAL, FOOD, CLOTHING, ELECTRONICS, TOY));
        places.add(new Place("apartment", "דירה", "דירות", Gender.FEMALE, PlaceType.HOME, GENERAL, FOOD, CLOTHING, ELECTRONICS, TOY));

        // ==========================================
        // מרחב ציבורי (PUBLIC)
        // ==========================================
        places.add(new Place("street", "רחוב", "רחובות", Gender.MALE, PlaceType.PUBLIC, GENERAL));
        places.add(new Place("square", "כיכר", "כיכרות", Gender.FEMALE, PlaceType.PUBLIC, GENERAL));
        places.add(new Place("city_hall", "בניין עירייה", "בנייני עירייה", Gender.MALE, PlaceType.PUBLIC, STATIONERY, GENERAL));

        // ==========================================
        // חיק הטבע (OUTDOORS)
        // ==========================================
        places.add(new Place("park", "פארק", "פארקים", Gender.MALE, PlaceType.OUTDOORS, GENERAL, FOOD));
        places.add(new Place("forest", "יער", "יערות", Gender.MALE, PlaceType.OUTDOORS, GENERAL));
        places.add(new Place("beach", "חוף ים", "חופי ים", Gender.MALE, PlaceType.OUTDOORS, GENERAL, FOOD));

        // ==========================================
        // חינוך ולימודים (EDUCATION)
        // ==========================================
        places.add(new Place("school", "בית ספר", "בתי ספר", Gender.MALE, PlaceType.EDUCATION, STATIONERY, GENERAL));
        places.add(new Place("library", "ספרייה", "ספריות", Gender.FEMALE, PlaceType.EDUCATION, STATIONERY, GENERAL));
        places.add(new Place("university", "אוניברסיטה", "אוניברסיטאות", Gender.FEMALE, PlaceType.EDUCATION, STATIONERY, ELECTRONICS, GENERAL));
        places.add(new Place("classroom", "כיתה", "כיתות", Gender.FEMALE, PlaceType.EDUCATION, STATIONERY));

        // ==========================================
        // פנאי ובידור (ENTERTAINMENT)
        // ==========================================
        places.add(new Place("cinema", "קולנוע", "בתי קולנוע", Gender.MALE, PlaceType.ENTERTAINMENT, FOOD, GENERAL));
        places.add(new Place("museum", "מוזיאון", "מוזיאונים", Gender.MALE, PlaceType.ENTERTAINMENT, ANTIQUE, COLLECTIBLE)); // מוזיאון מושלם לעתיקות
        places.add(new Place("amusement_park", "לונה פארק", "פארקי שעשועים", Gender.MALE, PlaceType.ENTERTAINMENT, FOOD, TOY));
        places.add(new Place("zoo", "גן חיות", "גני חיות", Gender.MALE, PlaceType.ENTERTAINMENT, FOOD, TOY));
        places.add(new Place("pool", "בריכה", "בריכות", Gender.FEMALE, PlaceType.ENTERTAINMENT, FOOD, GENERAL));

        // ==========================================
        // מסעדות ומזון (FOOD_SERVICE)
        // ==========================================
        places.add(new Place("restaurant", "מסעדה", "מסעדות", Gender.FEMALE, PlaceType.FOOD_SERVICE, FOOD));
        places.add(new Place("cafe", "בית קפה", "בתי קפה", Gender.MALE, PlaceType.FOOD_SERVICE, FOOD));
        places.add(new Place("pizzeria", "פיצרייה", "פיצריות", Gender.FEMALE, PlaceType.FOOD_SERVICE, FOOD));

        // ==========================================
        // תחבורה ונסיעות (TRANSPORTATION)
        // ==========================================
        places.add(new Place("train_station", "תחנת רכבת", "תחנות רכבת", Gender.FEMALE, PlaceType.TRANSPORTATION, GENERAL, FOOD));
        places.add(new Place("bus_station", "תחנת אוטובוס", "תחנות אוטובוס", Gender.FEMALE, PlaceType.TRANSPORTATION, GENERAL, FOOD));
        places.add(new Place("airport", "שדה תעופה", "שדות תעופה", Gender.MALE, PlaceType.TRANSPORTATION, GENERAL, FOOD, CLOTHING, ELECTRONICS));

        // ==========================================
        // ספורט ובריאות (HEALTH)
        // ==========================================
        places.add(new Place("hospital", "בית חולים", "בתי חולים", Gender.MALE, PlaceType.HEALTH, GENERAL, FOOD));
        places.add(new Place("clinic", "מרפאה", "מרפאות", Gender.FEMALE, PlaceType.HEALTH, GENERAL));
        places.add(new Place("pharmacy", "בית מרקחת", "בתי מרקחת", Gender.MALE, PlaceType.HEALTH, GENERAL)); // בהמשך נוסיף להם MEDICAL
    }

    public static void fillAdjectives() {
        // --- צבעים ---
        Adjective red = new Adjective("red", COLOR);
        red.addForm("MALE", "s", "אדום");
        red.addForm("FEMALE", "s", "אדומה");
        red.addForm("MALE", "p", "אדומים");
        red.addForm("FEMALE", "p", "אדומות");
        adjectives.add(red);

        Adjective blue = new Adjective("blue", COLOR);
        blue.addForm("MALE", "s", "כחול");
        blue.addForm("FEMALE", "s", "כחולה");
        blue.addForm("MALE", "p", "כחולים");
        blue.addForm("FEMALE", "p", "כחולות");
        adjectives.add(blue);

        // --- גדלים ---
        Adjective big = new Adjective("big", SIZE);
        big.addForm("MALE", "s", "גדול");
        big.addForm("FEMALE", "s", "גדולה");
        big.addForm("MALE", "p", "גדולים");
        big.addForm("FEMALE", "p", "גדולות");
        adjectives.add(big);

        Adjective small = new Adjective("small", SIZE);
        small.addForm("MALE", "s", "קטן");
        small.addForm("FEMALE", "s", "קטנה");
        small.addForm("MALE", "p", "קטנים");
        small.addForm("FEMALE", "p", "קטנות");
        adjectives.add(small);

        // --- תכונות שונות ---
        Adjective newAdj = new Adjective("new", CONDITION);
        newAdj.addForm("MALE", "s", "חדש");
        newAdj.addForm("FEMALE", "s", "חדשה");
        newAdj.addForm("MALE", "p", "חדשים");
        newAdj.addForm("FEMALE", "p", "חדשות");
        adjectives.add(newAdj);

        Adjective oldAdj = new Adjective("old", CONDITION);
        oldAdj.addForm("MALE", "s", "ישן");
        oldAdj.addForm("FEMALE", "s", "ישנה");
        oldAdj.addForm("MALE", "p", "ישנים");
        oldAdj.addForm("FEMALE", "p", "ישנות");
        adjectives.add(oldAdj);
    }

    public static void fillUnits() {
        // ==========================================
        // 1. משקל (WEIGHT) - בעיקר לאוכל, ולפעמים לעתיקות (זהב)
        // ==========================================
        units.add(new Unit("kg", "קילוגרם", "קילוגרמים", Gender.MALE, UnitType.WEIGHT, ItemCategory.FOOD, ItemCategory.ANTIQUE));
        units.add(new Unit("gram", "גרם", "גרמים", Gender.MALE, UnitType.WEIGHT, ItemCategory.FOOD, ItemCategory.ANTIQUE));
        units.add(new Unit("ton", "טון", "טונות", Gender.MALE, UnitType.WEIGHT, ItemCategory.FOOD, ItemCategory.ANTIQUE)); // כמו מטבעות זהב
        units.add(new Unit("sack", "שק", "שקים", Gender.MALE, UnitType.WEIGHT, ItemCategory.FOOD)); // שק קמח, שק תפוחי אדמה

        // ==========================================
        // 2. אורך (LENGTH) - לחומרים ציוד כללי
        // ==========================================
        units.add(new Unit("meter", "מטר", "מטרים", Gender.MALE, UnitType.LENGTH, ItemCategory.GENERAL, ItemCategory.ELECTRONICS));
        units.add(new Unit("cm", "סנטימטר", "סנטימטרים", Gender.MALE, UnitType.LENGTH, ItemCategory.GENERAL));
        units.add(new Unit("km", "קילומטר", "קילומטרים", Gender.MALE, UnitType.LENGTH, ItemCategory.GENERAL)); // אולי פחות רלוונטי לחנות, אבל טוב לבעיות תנועה
        units.add(new Unit("roll", "גליל", "גלילים", Gender.MALE, UnitType.LENGTH, ItemCategory.GENERAL)); // גליל של כבל חשמל או חבל

        // ==========================================
        // 3. נפח (VOLUME) - לפריטי שתייה ונוזלים
        // ==========================================
        units.add(new Unit("liter", "ליטר", "ליטרים", Gender.MALE, UnitType.VOLUME, ItemCategory.FOOD));
        units.add(new Unit("ml", "מיליליטר", "מיליליטרים", Gender.MALE, UnitType.VOLUME, ItemCategory.FOOD));
        units.add(new Unit("cup", "כוס", "כוסות", Gender.FEMALE, UnitType.VOLUME, ItemCategory.FOOD));
        units.add(new Unit("bottle", "בקבוק", "בקבוקים", Gender.MALE, UnitType.VOLUME, ItemCategory.FOOD)); // בקבוק מים/חלב

        // ==========================================
        // 4. ספירה (COUNT) - לארוז חפצים
        // ==========================================
        // יחידה - מילה כללית שלפעמים מתאימה לציוד ואלקטרוניקה
        units.add(new Unit("piece", "יחידה", "יחידות", Gender.FEMALE, UnitType.COUNT, ItemCategory.GENERAL, ItemCategory.ELECTRONICS, ItemCategory.STATIONERY));

        // מארז - מתאים לצעצועים, כלי כתיבה, אוכל יבש
        units.add(new Unit("box", "מארז", "מארזים", Gender.MALE, UnitType.COUNT, ItemCategory.FOOD, ItemCategory.TOY, ItemCategory.STATIONERY, ItemCategory.ELECTRONICS));

        // קרטון - אוכל, ציוד משרדי
        units.add(new Unit("carton", "קרטון", "קרטונים", Gender.MALE, UnitType.COUNT, ItemCategory.FOOD, ItemCategory.STATIONERY));

        // חבילה - קלפים, בולים, עפרונות, במבה
        units.add(new Unit("pack", "חבילה", "חבילות", Gender.FEMALE, UnitType.COUNT, ItemCategory.FOOD, ItemCategory.STATIONERY, ItemCategory.COLLECTIBLE));

        // מגש - מתאים *אך ורק* לאוכל! (מגש פיצה, מגש ביצים)
        units.add(new Unit("tray", "מגש", "מגשים", Gender.MALE, UnitType.COUNT, ItemCategory.FOOD));

        units.add(new Unit("none_unit", "", "", Gender.MALE, UnitType.NONE,
                ItemCategory.GENERAL, ItemCategory.ELECTRONICS, ItemCategory.CLOTHING, ItemCategory.TOY));
    }

    public static void fillRoles() {
        // ==========================================
        // 1. מפעילים (OPERATORS) - נותני השירות
        // ==========================================

        // -- חנויות ועסקים --
        roles.add(new RoleEntity("seller", "מוכר", "מוכרים", "מוכרת", "מוכרות", RoleType.OPERATOR,
                "grocery", "kiosk", "toy_store", "craft_store", "stationery_store", "generic_store", "clothing_store", "electronics_store"));

        roles.add(new RoleEntity("cashier", "קופאי", "קופאים", "קופאית", "קופאיות", RoleType.OPERATOR,
                "supermarket", "mall"));

        roles.add(new RoleEntity("baker", "אופה", "אופים", "אופה", "אופות", RoleType.OPERATOR,
                "bakery"));

        roles.add(new RoleEntity("merchant", "סוחר", "סוחרים", "סוחרת", "סוחרות", RoleType.OPERATOR,
                "market", "antique_store"));

        roles.add(new RoleEntity("pharmacist", "רוקח", "רוקחים", "רוקחת", "רוקחות", RoleType.OPERATOR,
                "pharmacy"));

        // -- חינוך ולימודים --
        roles.add(new RoleEntity("teacher", "מורה", "מורים", "מורה", "מורות", RoleType.OPERATOR,
                "school", "classroom"));

        roles.add(new RoleEntity("librarian", "ספרן", "ספרנים", "ספרנית", "ספרניות", RoleType.OPERATOR,
                "library"));

        roles.add(new RoleEntity("professor", "מרצה", "מרצים", "מרצה", "מרצות", RoleType.OPERATOR,
                "university"));

        // -- פנאי, בידור וטבע --
        roles.add(new RoleEntity("guide", "מדריך", "מדריכים", "מדריכה", "מדריכות", RoleType.OPERATOR,
                "museum", "zoo", "park", "forest"));

        roles.add(new RoleEntity("usher", "סדרן", "סדרנים", "סדרנית", "סדרניות", RoleType.OPERATOR,
                "cinema", "amusement_park"));

        roles.add(new RoleEntity("lifeguard", "מציל", "מצילים", "מצילה", "מצילות", RoleType.OPERATOR,
                "pool", "beach"));

        // -- מסעדות ומזון --
        roles.add(new RoleEntity("waiter", "מלצר", "מלצרים", "מלצרית", "מלצריות", RoleType.OPERATOR,
                "restaurant", "cafe", "pizzeria"));

        // -- תחבורה ונסיעות --
        roles.add(new RoleEntity("driver", "נהג", "נהגים", "נהגת", "נהגות", RoleType.OPERATOR,
                "bus_station", "train_station")); // אפשר גם להוסיף 'כרטיסן'

        // -- ספורט ובריאות --
        roles.add(new RoleEntity("doctor", "רופא", "רופאים", "רופאה", "רופאות", RoleType.OPERATOR,
                "hospital", "clinic"));

        roles.add(new RoleEntity("nurse", "אח", "אחים", "אחות", "אחיות", RoleType.OPERATOR,
                "hospital", "clinic"));


        // ==========================================
        // 2. מופעלים (TARGETS) - קהל היעד / מקבלי השירות
        // ==========================================

        // -- לקוחות קלאסיים --
        roles.add(new RoleEntity("customer", "לקוח", "לקוחות", "לקוחה", "לקוחות", RoleType.TARGET,
                "grocery", "supermarket", "bakery", "kiosk", "toy_store", "craft_store", "stationery_store",
                "generic_store", "mall", "clothing_store", "electronics_store", "pharmacy"));

        roles.add(new RoleEntity("buyer", "קונה", "קונים", "קונה", "קונות", RoleType.TARGET,
                "market", "generic_store"));

        // -- חינוך ולימודים --
        roles.add(new RoleEntity("student", "תלמיד", "תלמידים", "תלמידה", "תלמידות", RoleType.TARGET,
                "school", "classroom", "stationery_store"));

        roles.add(new RoleEntity("university_student", "סטודנט", "סטודנטים", "סטודנטית", "סטודנטיות", RoleType.TARGET,
                "university"));

        roles.add(new RoleEntity("reader", "קורא", "קוראים", "קוראת", "קוראות", RoleType.TARGET,
                "library"));

        // -- פנאי ובידור --
        roles.add(new RoleEntity("visitor", "מבקר", "מבקרים", "מבקרת", "מבקרות", RoleType.TARGET,
                "mall", "market", "museum", "zoo", "amusement_park", "park"));

        roles.add(new RoleEntity("viewer", "צופה", "צופים", "צופה", "צופות", RoleType.TARGET,
                "cinema"));

        roles.add(new RoleEntity("collector", "אספן", "אספנים", "אספנית", "אספניות", RoleType.TARGET,
                "antique_store", "market"));

        roles.add(new RoleEntity("traveler", "מטייל", "מטיילים", "מטיילת", "מטיילות", RoleType.TARGET,
                "forest", "park", "street", "square"));

        roles.add(new RoleEntity("bather", "מתרחץ", "מתרחצים", "מתרחצת", "מתרחצות", RoleType.TARGET,
                "pool", "beach"));

        // -- מזון, בריאות ותחבורה --
        roles.add(new RoleEntity("diner", "סועד", "סועדים", "סועדת", "סועדות", RoleType.TARGET,
                "restaurant", "pizzeria", "cafe"));

        roles.add(new RoleEntity("patient", "מטופל", "מטופלים", "מטופלת", "מטופלות", RoleType.TARGET,
                "hospital", "clinic"));

        roles.add(new RoleEntity("passenger", "נוסע", "נוסעים", "נוסעת", "נוסעות", RoleType.TARGET,
                "train_station", "bus_station", "airport"));

        roles.add(new RoleEntity("pedestrian", "הולך רגל", "הולכי רגל", "הולכת רגל", "הולכות רגל", RoleType.TARGET,
                "street", "square"));
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
