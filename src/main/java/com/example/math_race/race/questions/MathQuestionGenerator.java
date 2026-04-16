package com.example.math_race.race.questions;

import com.example.math_race.questionGenerator.QuestionEngine;
import com.example.math_race.questionGenerator.tags.core.TemplateTag;
import com.example.math_race.questionGenerator.tags.core.TagInfo;
import com.example.math_race.questionGenerator.tags.enums.*;
import com.example.math_race.questionGenerator.tags.types.*;
import com.example.math_race.race.RacePlayer;
import com.example.math_race.service.QuestionTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.math_race.questionGenerator.tags.enums.Gender.*;

@Component
public class MathQuestionGenerator {

    private final QuestionEngine questionEngine;
    private final QuestionTemplateService questionTemplateService;

    @Autowired
    public MathQuestionGenerator(QuestionEngine questionEngine, QuestionTemplateService questionTemplateService) {
        this.questionEngine = questionEngine;
        this.questionTemplateService = questionTemplateService;
    }


    static ArrayList<HumanTag> humans;
    static ArrayList<ItemTag> items;
    static ArrayList<VerbTag> verbs;
    static ArrayList<PlaceTag> places;
    public static List<AdjectiveTag> adjectives;
    public static List<UnitTag> units;
    private static final List<RoleTag> roles;
    private static final List<VehicleTag> vehicles;

    static {
        humans = fillHumans();
        verbs = fillVerbs();
        items = fillItems();
        places = fillPlaces();
        adjectives = fillAdjectives();
        units = fillUnits();
        roles = fillRoles();
        vehicles = fillVehicles();
    }


    String template = "[HUMAN:g=?:#1] [VERB:id=buy;g=(#1:g);t=past;num=s:#2] [NUM:min=2;max=10:mul_3:#3] [ITEM:type=FOOD:p:#4]";
// זה יכול להדפיס: "Noa קנתה 5 apples" או "Shimon קנה 3 bananas"

    String t = "[ITEM:param=m;param=?:take:name]";


    public static String gene(String template, Map<String, TemplateTag> memory) {
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

                TemplateTag chosen = null;
                if ("HUMAN".equals(info.getType())) chosen = findHuman(resolvedConstraints);
                else if ("ITEM".equals(info.getType())) chosen = findItem(resolvedConstraints);
                else if ("NUM".equals(info.getType())) chosen = findNumber(resolvedConstraints);
                else if ("VERB".equals(info.getType())) chosen = findVerb(resolvedConstraints);
                else if ("PLACE".equals(info.getType())) chosen = findPlace(resolvedConstraints);
                else if ("ADJ".equals(info.getType())) chosen = findAdjective(resolvedConstraints);
                else if ("UNIT".equals(info.getType())) chosen = findUnit(resolvedConstraints);
                else if ("TIME".equals(info.getType())) chosen = findTime(resolvedConstraints);
                else if ("ROLE".equals(info.getType())) chosen = findRole(resolvedConstraints);
                else if ("VEHICLE".equals(info.getType())) chosen = findVehicle(resolvedConstraints);

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

    private static String resolveValue(String value, Map<String, TemplateTag> memory) {
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

    public static TemplateTag findAdjective(Map<String, String> constraints) {
        List<AdjectiveTag> matches = adjectives.stream()
                .filter(a -> a.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No adjective matches constraints: " + constraints);
            return null;
        }

        return  matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));
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

    private static NumberTag findNumber(Map<String, String> constraints) {
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

    private static TimeTag findTime(Map<String, String> constraints) {
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

    public static TemplateTag findVerb(Map<String, String> constraints) {
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
            return key -> chosenVerb.getWord("inf", MALE, "ANY");
        }

        String exactWord = "";//chosenVerb.getWord(t, g, num);

        return key -> exactWord;
    }

    public static TemplateTag findUnit(Map<String, String> constraints) {
        List<UnitTag> matches = units.stream()
                .filter(u -> u.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No unit matches constraints: " + constraints);
            return null;
        }

        return matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static TemplateTag findRole(Map<String, String> constraints) {
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

    public static TemplateTag findVehicle(Map<String, String> constraints) {
        List<VehicleTag> matches = vehicles.stream()
                .filter(v -> v.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No vehicle matches constraints: " + constraints);
            return null;
        }

        return matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static ArrayList<HumanTag> fillHumans() {
        ArrayList<HumanTag> humans = new ArrayList<>();
        // --- בנים (25) ---
        humans.add(new HumanTag("שמעון", MALE));
        humans.add(new HumanTag("יוסף", MALE));
        humans.add(new HumanTag("אברהם", MALE));
        humans.add(new HumanTag("דוד", MALE));
        humans.add(new HumanTag("משה", MALE));
        humans.add(new HumanTag("אריאל", MALE));
        humans.add(new HumanTag("נועם", MALE));
        humans.add(new HumanTag("איתי", MALE));
        humans.add(new HumanTag("אורי", MALE));
        humans.add(new HumanTag("עומר", MALE));
        humans.add(new HumanTag("דניאל", MALE));
        humans.add(new HumanTag("יהונתן", MALE));
        humans.add(new HumanTag("רועי", MALE));
        humans.add(new HumanTag("עידן", MALE));
        humans.add(new HumanTag("עמית", MALE));
        humans.add(new HumanTag("גיא", MALE));
        humans.add(new HumanTag("מאור", MALE));
        humans.add(new HumanTag("תומר", MALE));
        humans.add(new HumanTag("אלעד", MALE));
        humans.add(new HumanTag("ירון", MALE));
        humans.add(new HumanTag("אורן", MALE));
        humans.add(new HumanTag("ברק", MALE));
        humans.add(new HumanTag("גלעד", MALE));
        humans.add(new HumanTag("ניר", MALE));
        humans.add(new HumanTag("אסף", MALE));

        // --- בנות (25) ---
        humans.add(new HumanTag("נועה", FEMALE));
        humans.add(new HumanTag("תמר", FEMALE));
        humans.add(new HumanTag("יעל", FEMALE));
        humans.add(new HumanTag("מאיה", FEMALE));
        humans.add(new HumanTag("אביגיל", FEMALE));
        humans.add(new HumanTag("טליה", FEMALE));
        humans.add(new HumanTag("עדי", FEMALE));
        humans.add(new HumanTag("שירה", FEMALE));
        humans.add(new HumanTag("מיכל", FEMALE));
        humans.add(new HumanTag("רוני", FEMALE));
        humans.add(new HumanTag("אלה", FEMALE));
        humans.add(new HumanTag("עדן", FEMALE));
        humans.add(new HumanTag("הילה", FEMALE));
        humans.add(new HumanTag("דנה", FEMALE));
        humans.add(new HumanTag("מורן", FEMALE));
        humans.add(new HumanTag("קרן", FEMALE));
        humans.add(new HumanTag("ענת", FEMALE));
        humans.add(new HumanTag("מירי", FEMALE));
        humans.add(new HumanTag("גלי", FEMALE));
        humans.add(new HumanTag("סיון", FEMALE));
        humans.add(new HumanTag("רותם", FEMALE)); // מתאים גם לבנים, אבל הגדרנו כבת
        humans.add(new HumanTag("שיר", FEMALE));
        humans.add(new HumanTag("אור", FEMALE));   // כנ"ל
        humans.add(new HumanTag("שחר", FEMALE));   // כנ"ל
        humans.add(new HumanTag("ענבל", FEMALE));

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
        items.add(new ItemTag("apple", "תפוח", "תפוחים", MALE, weightOrCount, ItemCategory.PRODUCE));
        items.add(new ItemTag("tomato", "עגבנייה", "עגבניות", FEMALE, weightOrCount, ItemCategory.PRODUCE));
        items.add(new ItemTag("bread_loaf", "כיכר לחם", "כיכרות לחם", MALE, countOnly, ItemCategory.BAKED_GOODS));
        items.add(new ItemTag("croissant", "קרואסון", "קרואסונים", MALE, countOnly, ItemCategory.BAKED_GOODS));
        items.add(new ItemTag("water", "מים", "מים", MALE, volumeOrCount, ItemCategory.DRINKS));
        items.add(new ItemTag("candy", "סוכרייה", "סוכריות", FEMALE, weightOrCount, ItemCategory.SWEETS));
        items.add(new ItemTag("popcorn_bag", "שקית פופקורן", "שקיות פופקורן", MALE, weightOrCount, ItemCategory.SWEETS));
        items.add(new ItemTag("flour_bag", "שק קמח", "שקי קמח", MALE, weightOnly, ItemCategory.GENERAL_FOOD));

// --- ART SUPPLIES (יצירה - חדש!) ---
        items.add(new ItemTag("paintbrush", "מכחול", "מכחולים", MALE, countOnly, ItemCategory.ART_SUPPLIES));
        items.add(new ItemTag("canvas", "קנבס", "קנבסים", MALE, countOnly, ItemCategory.ART_SUPPLIES));
        items.add(new ItemTag("paint_tube", "שפופרת צבע", "שפופרות צבע", FEMALE, countOnly, ItemCategory.ART_SUPPLIES));
        items.add(new ItemTag("clay", "חימר", "גושי חימר", MALE, weightOrCount, ItemCategory.ART_SUPPLIES));

// --- STATIONERY (כלי כתיבה) ---
        items.add(new ItemTag("pencil", "עיפרון", "עפרונות", MALE, countOnly, ItemCategory.STATIONERY));
        items.add(new ItemTag("notebook", "מחברת", "מחברות", FEMALE, countOnly, ItemCategory.STATIONERY));
        items.add(new ItemTag("eraser", "מחק", "מחקים", MALE, countOnly, ItemCategory.STATIONERY));
        items.add(new ItemTag("ruler", "סרגל", "סרגלים", MALE, countOnly, ItemCategory.STATIONERY));

// --- ANTIQUE & COLLECTIBLE (עתיקות ואספנות) ---
        items.add(new ItemTag("gold_coin", "מטבע זהב", "מטבעות זהב", MALE, weightOrCount, ItemCategory.ANTIQUE, ItemCategory.COLLECTIBLE));
        items.add(new ItemTag("clay_jug", "כד חרס", "כדי חרס", MALE, countOnly, ItemCategory.ANTIQUE));
        items.add(new ItemTag("pokemon_card", "קלף פוקימון", "קלפי פוקימון", MALE, countOnly, ItemCategory.COLLECTIBLE));
        items.add(new ItemTag("pop_figure", "בובת פופ", "בובות פופ", FEMALE, countOnly, ItemCategory.COLLECTIBLE));

// --- MEDICAL (רפואי) ---
        items.add(new ItemTag("medicine", "תרופה", "תרופות", FEMALE, countOnly, ItemCategory.MEDICAL));
        items.add(new ItemTag("bandage", "פלסטר", "פלסטרים", MALE, countOnly, ItemCategory.MEDICAL));

// --- TOY (צעצועים) ---
        items.add(new ItemTag("doll", "בובה", "בובות", FEMALE, countOnly, ItemCategory.TOY));
        items.add(new ItemTag("puzzle", "פאזל", "פאזלים", MALE, countOnly, ItemCategory.TOY));

// --- CLOTHING (ביגוד) ---
        items.add(new ItemTag("shirt", "חולצה", "חולצות", FEMALE, noUnit, ItemCategory.CLOTHING));
        items.add(new ItemTag("pants", "מכנס", "מכנסיים", MALE, noUnit, ItemCategory.CLOTHING));

// --- ELECTRONICS (אלקטרוניקה) ---
        items.add(new ItemTag("laptop", "מחשב נייד", "מחשבים ניידים", MALE, countOnly, ItemCategory.ELECTRONICS));
        items.add(new ItemTag("earphone", "אוזנייה", "אוזניות", FEMALE, countOnly, ItemCategory.ELECTRONICS));

// --- HARDWARE (חומרי בניין ועבודה - התאמה לאורך) ---
        items.add(new ItemTag("power_cable", "כבל חשמל", "כבלי חשמל", MALE, lengthOnly, ItemCategory.HARDWARE, ItemCategory.ELECTRONICS));
        items.add(new ItemTag("rope", "חבל", "חבלים", MALE, lengthOnly, ItemCategory.HARDWARE));
        items.add(new ItemTag("pipe", "צינור", "צינורות", MALE, lengthOnly, ItemCategory.HARDWARE));

// פריטי כלי עבודה וחומרי בניין (HARDWARE) - ספירים
        items.add(new ItemTag("hammer", "פטיש", "פטישים", MALE, countOnly, ItemCategory.HARDWARE));
        items.add(new ItemTag("screwdriver", "מברג", "מברגים", MALE, countOnly, ItemCategory.HARDWARE));
        items.add(new ItemTag("nail", "מסמר", "מסמרים", MALE, countOnly, ItemCategory.HARDWARE));
        items.add(new ItemTag("screw", "בורג", "ברגים", MALE, countOnly, ItemCategory.HARDWARE));
        items.add(new ItemTag("lock", "מנעול", "מנעולים", MALE, countOnly, ItemCategory.HARDWARE));
        items.add(new ItemTag("wrench", "מפתח ברגים", "מפתחות ברגים", MALE, countOnly, ItemCategory.HARDWARE));
        items.add(new ItemTag("paint_brush", "מברשת צבע", "מברשות צבע", FEMALE, countOnly, ItemCategory.HARDWARE));

        return  items;
    }



    public static ArrayList<VerbTag> fillVerbs() {
        ArrayList<VerbTag> verbs = new ArrayList<>();
        // --- קנה ---
        VerbTag buy = new VerbTag("buy");
        buy.addForm("past", MALE, "s", "קנה");
        buy.addForm("past", FEMALE, "s", "קנתה");
        buy.addForm("past", MALE, "p", "קנו");
        buy.addForm("past", FEMALE, "p", "קנו");
        buy.addForm("inf", MALE, "ANY", "לקנות");
        verbs.add(buy);

        // --- אכל (לאוכל, מאפים וממתקים) ---
        VerbTag eat = new VerbTag("eat");
        eat.addForm("past", MALE, "s", "אכל");
        eat.addForm("past", FEMALE, "s", "אכלה");
        eat.addForm("past", MALE, "p", "אכלו");
        eat.addForm("past", FEMALE, "p", "אכלו");
        eat.addForm("inf", MALE, "ANY", "לאכול");
        verbs.add(eat);

        // --- שתה (חדש - למשקאות!) ---
        VerbTag drink = new VerbTag("drink");
        drink.addForm("past", MALE, "s", "שתה");
        drink.addForm("past", FEMALE, "s", "שתתה");
        drink.addForm("past", MALE, "p", "שתו");
        drink.addForm("past", FEMALE, "p", "שתו");
        drink.addForm("inf", MALE, "ANY", "לשתות");
        verbs.add(drink);

        // --- לבש (חדש - לביגוד!) ---
        VerbTag wear = new VerbTag("wear");
        wear.addForm("past", MALE, "s", "לבש");
        wear.addForm("past", FEMALE, "s", "לבשה");
        wear.addForm("past", MALE, "p", "לבשו");
        wear.addForm("past", FEMALE, "p", "לבשו");
        wear.addForm("inf", MALE, "ANY", "ללבוש");
        verbs.add(wear);

        // --- תיקן (חדש - לטמבורייה ואלקטרוניקה!) ---
        VerbTag fix = new VerbTag("fix");
        fix.addForm("past", MALE, "s", "תיקן");
        fix.addForm("past", FEMALE, "s", "תיקנה");
        fix.addForm("past", MALE, "p", "תיקנו");
        fix.addForm("past", FEMALE, "p", "תיקנו");
        fix.addForm("inf", MALE, "ANY", "לתקן");
        verbs.add(fix);

        // --- נתן ---
        VerbTag give = new VerbTag("give");
        give.addForm("past", MALE, "s", "נתן");
        give.addForm("past", FEMALE, "s", "נתנה");
        give.addForm("past", MALE, "p", "נתנו");
        give.addForm("past", FEMALE, "p", "נתנו");
        give.addForm("inf", MALE, "ANY", "לתת");
        verbs.add(give);

        // --- קיבל ---
        VerbTag receive = new VerbTag("receive");
        receive.addForm("past", MALE, "s", "קיבל");
        receive.addForm("past", FEMALE, "s", "קיבלה");
        receive.addForm("past", MALE, "p", "קיבלו");
        receive.addForm("past", FEMALE, "p", "קיבלו");
        receive.addForm("inf", MALE, "ANY", "לקבל");
        verbs.add(receive);

        // --- מצא ---
        VerbTag find = new VerbTag("find");
        find.addForm("past", MALE, "s", "מצא");
        find.addForm("past", FEMALE, "s", "מצאה");
        find.addForm("past", MALE, "p", "מצאו");
        find.addForm("past", FEMALE, "p", "מצאו");
        find.addForm("inf", MALE, "ANY", "למצוא");
        verbs.add(find);

        // --- איבד ---
        VerbTag lose = new VerbTag("lose");
        lose.addForm("past", MALE, "s", "איבד");
        lose.addForm("past", FEMALE, "s", "איבדה");
        lose.addForm("past", MALE, "p", "איבדו");
        lose.addForm("past", FEMALE, "p", "איבדו");
        lose.addForm("inf", MALE, "ANY", "לאבד");
        verbs.add(lose);

        // --- אסף ---
        VerbTag collect = new VerbTag("collect");
        collect.addForm("past", MALE, "s", "אסף");
        collect.addForm("past", FEMALE, "s", "אספה");
        collect.addForm("past", MALE, "p", "אספו");
        collect.addForm("past", FEMALE, "p", "אספו");
        collect.addForm("inf", MALE, "ANY", "לאסוף");
        verbs.add(collect);

        // --- חילק ---
        VerbTag divide = new VerbTag("divide");
        divide.addForm("past", MALE, "s", "חילק");
        divide.addForm("past", FEMALE, "s", "חילקה");
        divide.addForm("past", MALE, "p", "חילקו");
        divide.addForm("past", FEMALE, "p", "חילקו");
        divide.addForm("inf", MALE, "ANY", "לחלק");
        verbs.add(divide);

        // --- נכנס ---
        VerbTag enter = new VerbTag("enter");
        enter.addForm("past", MALE, "s", "נכנס");
        enter.addForm("past", FEMALE, "s", "נכנסה");
        enter.addForm("past", MALE, "p", "נכנסו");
        enter.addForm("past", FEMALE, "p", "נכנסו");
        enter.addForm("inf", MALE, "ANY", "להיכנס");
        verbs.add(enter);

        // --- מכר ---
        VerbTag sell = new VerbTag("sell");
        sell.addForm("past", MALE, "s", "מכר");
        sell.addForm("past", FEMALE, "s", "מכרה");
        sell.addForm("past", MALE, "p", "מכרו");
        sell.addForm("past", FEMALE, "p", "מכרו");
        sell.addForm("inf", MALE, "ANY", "למכור");
        verbs.add(sell);

        // --- לקח ---
        VerbTag take = new VerbTag("take");
        take.addForm("past", MALE, "s", "לקח");
        take.addForm("past", FEMALE, "s", "לקחה");
        take.addForm("past", MALE, "p", "לקחו");
        take.addForm("past", FEMALE, "p", "לקחו");
        take.addForm("inf", MALE, "ANY", "לקחת");
        verbs.add(take);

        // --- שם / הניח ---
        VerbTag put = new VerbTag("put");
        put.addForm("past", MALE, "s", "שם");
        put.addForm("past", FEMALE, "s", "שמה");
        put.addForm("past", MALE, "p", "שמו");
        put.addForm("past", FEMALE, "p", "שמו");
        put.addForm("inf", MALE, "ANY", "לשים");
        verbs.add(put);

        // --- סידר ---
        VerbTag arrange = new VerbTag("arrange");
        arrange.addForm("past", MALE, "s", "סידר");
        arrange.addForm("past", FEMALE, "s", "סידרה");
        arrange.addForm("past", MALE, "p", "סידרו");
        arrange.addForm("past", FEMALE, "p", "סידרו");
        arrange.addForm("inf", MALE, "ANY", "לסדר");

        arrange.addForm("present", MALE, "s", "מסדר");
        arrange.addForm("present", FEMALE, "s", "מסדרת");
        arrange.addForm("present", MALE, "p", "מסדרים");
        arrange.addForm("present", FEMALE, "p", "מסדרות");
        verbs.add(arrange);

        // --- שילם ---
        VerbTag pay = new VerbTag("pay");
        pay.addForm("past", MALE, "s", "שילם");
        pay.addForm("past", FEMALE, "s", "שילמה");
        pay.addForm("past", MALE, "p", "שילמו");
        pay.addForm("past", FEMALE, "p", "שילמו");
        pay.addForm("inf", MALE, "ANY", "לשלם");

        pay.addForm("future", MALE, "s", "ישלם");
        pay.addForm("future", FEMALE, "s", "תשלם");
        pay.addForm("future", MALE, "p", "ישלמו");
        pay.addForm("future", FEMALE, "p", "ישלמו");

        verbs.add(pay);

        // --- חסך ---
        VerbTag save = new VerbTag("save");
        save.addForm("past", MALE, "s", "חסך");
        save.addForm("past", FEMALE, "s", "חסכה");
        save.addForm("past", MALE, "p", "חסכו");
        save.addForm("past", FEMALE, "p", "חסכו");
        save.addForm("inf", MALE, "ANY", "לחסוך");
        save.addForm("present", MALE, "s", "חוסך");
        save.addForm("present", FEMALE, "s", "חוסכת");
        save.addForm("present", MALE, "p", "חוסכים");
        save.addForm("present", FEMALE, "p", "חוסכות");
        verbs.add(save);

        // --- סיים ---
        VerbTag finish = new VerbTag("finish");
        finish.addForm("past", MALE, "s", "סיים");
        finish.addForm("past", FEMALE, "s", "סיימה");
        finish.addForm("past", MALE, "p", "סיימו");
        finish.addForm("past", FEMALE, "p", "סיימו");
        finish.addForm("inf", MALE, "ANY", "לסיים");
        verbs.add(finish);

        // --- פעלים מיוחדים לתבניות (רצה, צריך, הציע) ---
        VerbTag offer = new VerbTag("offer");
        offer.addForm("past", MALE, "s", "הציע");
        offer.addForm("past", FEMALE, "s", "הציעה");
        offer.addForm("past", MALE, "p", "הציעו");
        offer.addForm("past", FEMALE, "p", "הציעו");
        offer.addForm("inf", MALE, "ANY", "להציע");
        verbs.add(offer);

        VerbTag want = new VerbTag("want");
        want.addForm("past", MALE, "s", "רצה");
        want.addForm("past", FEMALE, "s", "רצתה");
        want.addForm("past", MALE, "p", "רצו");
        want.addForm("past", FEMALE, "p", "רצו");
        want.addForm("inf", MALE, "ANY", "לרצות");

        want.addForm("present", MALE, "s", "רוצה");
        want.addForm("present", FEMALE, "s", "רוצה");
        want.addForm("present", MALE, "p", "רוצים");
        want.addForm("present", FEMALE, "p", "רוצות");
        verbs.add(want);

        VerbTag need = new VerbTag("need");
        need.addForm("past", MALE, "s", "היה צריך");
        need.addForm("past", FEMALE, "s", "הייתה צריכה");
        need.addForm("past", MALE, "p", "היו צריכים");
        need.addForm("past", FEMALE, "p", "היו צריכות");
        need.addForm("inf", MALE, "ANY", "להצטרך");
        verbs.add(need);

        VerbTag be = new VerbTag("be");
        be.addForm("past", MALE, "s", "היה");
        be.addForm("past", FEMALE, "s", "הייתה");
        be.addForm("past", MALE, "p", "היו");
        be.addForm("past", FEMALE, "p", "היו");
        be.addForm("inf", MALE, "ANY", "להיות");
        verbs.add(be);

        VerbTag can = new VerbTag("can");
        can.addForm("past", MALE, "s", "יכל");
        can.addForm("past", FEMALE, "s", "יכלה");
        can.addForm("past", MALE, "p", "יכלו");
        can.addForm("past", FEMALE, "p", "יכלו");
        can.addForm("inf", MALE, "ANY", "להצליח");

        can.addForm("present", MALE, "s", "יכול");
        can.addForm("present", FEMALE, "s", "יכולה");
        can.addForm("present", MALE, "p", "יכולים");
        can.addForm("present", FEMALE, "p", "יכולות");
        verbs.add(can);

        VerbTag sit = new VerbTag("sit");
        sit.addForm("past", MALE, "s", "ישב");
        sit.addForm("past", FEMALE, "s", "ישבה");
        sit.addForm("past", MALE, "p", "ישבו");
        sit.addForm("past", FEMALE, "p", "ישבו");
        sit.addForm("inf", MALE, "ANY", "לשבת");
        verbs.add(sit);

        VerbTag work = new VerbTag("work");
        work.addForm("past", MALE, "s", "עבד");
        work.addForm("past", FEMALE, "s", "עבדה");
        work.addForm("past", MALE, "p", "עבדו");
        work.addForm("past", FEMALE, "p", "עבדו");
        work.addForm("inf", MALE, "ANY", "לעבוד");
        verbs.add(work);

        // --- הוסיף ---
        VerbTag add = new VerbTag("add");

        add.addForm("past", MALE, "s", "הוסיף");
        add.addForm("past", FEMALE, "s", "הוסיפה");
        add.addForm("past", MALE, "p", "הוסיפו");
        add.addForm("past", FEMALE, "p", "הוסיפו");
        add.addForm("inf", MALE, "ANY", "להוסיף");

        add.addForm("present", MALE, "s", "מוסיף");
        add.addForm("present", FEMALE, "s", "מוסיפה");
        add.addForm("present", MALE, "p", "מוסיפים");
        add.addForm("present", FEMALE, "p", "מוסיפות");


        add.addForm("future", MALE, "s", "יוסיף");
        add.addForm("future", FEMALE, "s", "תוסיף");
        add.addForm("future", MALE, "p", "יוסיפו");
        add.addForm("future", FEMALE, "p", "יוסיפו");
        verbs.add(add);

        VerbTag read = new VerbTag("read");
        read.addForm("past", MALE, "s", "קרא");
        read.addForm("past", FEMALE, "s", "קראה");
        read.addForm("past", MALE, "p", "קראו");
        read.addForm("past", FEMALE, "p", "קראו");

        read.addForm("present", MALE, "s", "קורא");
        read.addForm("present", FEMALE, "s", "קוראת");
        read.addForm("present", MALE, "p", "קוראים");
        read.addForm("present", FEMALE, "p", "קוראות");

        read.addForm("inf", MALE, "ANY", "לקרוא");
        verbs.add(read);

        verbs.add(add);

        VerbTag cut = new VerbTag("cut");
        cut.addForm("past", MALE, "s", "חתך");
        cut.addForm("past", FEMALE, "s", "חתכה");
        add.addForm("past", MALE, "p", "חתכו");
        add.addForm("past", FEMALE, "p", "חתכו");

        cut.addForm("inf", MALE, "ANY", "לחתוך");
        verbs.add(cut);

        // --- הלך (walk) ---
        VerbTag walk = new VerbTag("walk");
        walk.addForm("past", MALE, "s", "הלך");
        walk.addForm("past", FEMALE, "s", "הלכה");
        walk.addForm("past", MALE, "p", "הלכו");
        walk.addForm("past", FEMALE, "p", "הלכו");
        walk.addForm("present", MALE, "s", "הולך");
        walk.addForm("present", FEMALE, "s", "הולכת");
        walk.addForm("present", MALE, "p", "הולכים");
        walk.addForm("present", FEMALE, "p", "הולכות");
        walk.addForm("inf", MALE, "ANY", "ללכת");
        verbs.add(walk);

        // הפועל "הפסיד / מפסיד" (lose_game) - מזהה חדש למניעת התנגשויות
        VerbTag loseGame = new VerbTag("lose_game");
        loseGame.addForm("present", MALE, "s", "מפסיד");
        loseGame.addForm("present", FEMALE, "s", "מפסידה");
        loseGame.addForm("past", MALE, "s", "הפסיד");
        loseGame.addForm("past", FEMALE, "s", "הפסידה");
        loseGame.addForm("past", MALE, "p", "הפסידו");
        loseGame.addForm("past", FEMALE, "p", "הפסידו");
        loseGame.addForm("inf", MALE, "ANY", "להפסיד");
        verbs.add(loseGame);

        VerbTag run = new VerbTag("run");
        run.addForm("present", MALE, "s", "רץ");
        run.addForm("present", FEMALE, "s", "רצה");
        run.addForm("past", MALE, "s", "רץ");
        run.addForm("past", FEMALE, "s", "רצה");
        verbs.add(run);


// --- הכין (prepare) ---
        VerbTag prepare = new VerbTag("prepare");
        prepare.addForm("past", MALE, "s", "הכין");
        prepare.addForm("past", FEMALE, "s", "הכינה");
        prepare.addForm("past", MALE, "p", "הכינו");
        prepare.addForm("past", FEMALE, "p", "הכינו");
        prepare.addForm("present", MALE, "s", "מכין");
        prepare.addForm("present", FEMALE, "s", "מכינה");
        prepare.addForm("present", MALE, "p", "מכינים");
        prepare.addForm("present", FEMALE, "p", "מכינות");
        prepare.addForm("inf", MALE, "ANY", "להכין");
        verbs.add(prepare);

// --- בחר (choose) ---
        VerbTag choose = new VerbTag("choose");
        choose.addForm("past", MALE, "s", "בחר");
        choose.addForm("past", FEMALE, "s", "בחרה");
        choose.addForm("past", MALE, "p", "בחרו");
        choose.addForm("past", FEMALE, "p", "בחרו");
        choose.addForm("present", MALE, "s", "בוחר");
        choose.addForm("present", FEMALE, "s", "בוחרת");
        choose.addForm("present", MALE, "p", "בוחרים");
        choose.addForm("present", FEMALE, "p", "בוחרות");
        choose.addForm("inf", MALE, "ANY", "לבחור");
        verbs.add(choose);

// --- כפל (multiply) ---
        VerbTag multiply = new VerbTag("multiply");
        multiply.addForm("past", MALE, "s", "כפל");
        multiply.addForm("past", FEMALE, "s", "כפלה");
        multiply.addForm("past", MALE, "p", "כפלו");
        multiply.addForm("past", FEMALE, "p", "כפלו");
        multiply.addForm("present", MALE, "s", "כופל");
        multiply.addForm("present", FEMALE, "s", "כופלת");
        multiply.addForm("present", MALE, "p", "כופלים");
        multiply.addForm("present", FEMALE, "p", "כופלות");
        multiply.addForm("inf", MALE, "ANY", "לכפול");
        verbs.add(multiply);

        VerbTag organize = new VerbTag("organize");
        organize.addForm("past", MALE, "s", "סידר");
        organize.addForm("past", FEMALE, "s", "סידרה");
        organize.addForm("past", MALE, "p", "סידרו");
        organize.addForm("past", FEMALE, "p", "סידרו");
        organize.addForm("inf", MALE, "ANY", "לסדר");
        verbs.add(organize);

        VerbTag start = new VerbTag("start");
        start.addForm("past", MALE, "s", "התחיל");
        start.addForm("past", FEMALE, "s", "התחילה");
        start.addForm("past", MALE, "p", "התחילו");
        start.addForm("past", FEMALE, "p", "התחילו");
        start.addForm("inf", MALE, "ANY", "להתחיל");
        verbs.add(start);

        // הפועל "שיחק / משחק" (play)
        VerbTag play = new VerbTag("play");
        play.addForm("present", MALE, "s", "משחק");
        play.addForm("present", FEMALE, "s", "משחקת");
        play.addForm("present", MALE, "p", "משחקים");
        play.addForm("present", FEMALE, "p", "משחקות");
        play.addForm("past", MALE, "s", "שיחק");
        play.addForm("past", FEMALE, "s", "שיחקה");
        play.addForm("past", MALE, "p", "שיחקו");
        play.addForm("past", FEMALE, "p", "שיחקו");
        play.addForm("inf", MALE, "ANY", "לשחק");
        verbs.add(play);

// הפועל "ניצח / מנצח" (win)
        VerbTag win = new VerbTag("win");
        win.addForm("present", MALE, "s", "מנצח");
        win.addForm("present", FEMALE, "s", "מנצחת");
        win.addForm("present", MALE, "p", "מנצחים");
        win.addForm("present", FEMALE, "p", "מנצחות");
        win.addForm("past", MALE, "s", "ניצח");
        win.addForm("past", FEMALE, "s", "ניצחה");
        win.addForm("past", MALE, "p", "ניצחו");
        win.addForm("past", FEMALE, "p", "ניצחו");
        win.addForm("inf", MALE, "ANY", "לנצח");
        verbs.add(win);

        return verbs;
    }

    public static ArrayList<PlaceTag> fillPlaces() {
        ArrayList<PlaceTag> places = new ArrayList<>();
        // --- חנויות מזון ---
        places.add(new PlaceTag("supermarket", "סופרמרקט", "סופרמרקטים", MALE, PlaceType.STORE,
                ItemCategory.PRODUCE, ItemCategory.BAKED_GOODS, ItemCategory.DRINKS, ItemCategory.SWEETS, ItemCategory.GENERAL_FOOD));
        places.add(new PlaceTag("grocery", "מכולת", "מכולות", FEMALE, PlaceType.STORE,
                ItemCategory.PRODUCE, ItemCategory.BAKED_GOODS, ItemCategory.DRINKS, ItemCategory.SWEETS, ItemCategory.GENERAL_FOOD));
        places.add(new PlaceTag("bakery", "מאפייה", "מאפיות", FEMALE, PlaceType.STORE,
                ItemCategory.BAKED_GOODS, ItemCategory.DRINKS));
        places.add(new PlaceTag("kiosk", "קיוסק", "קיוסקים", MALE, PlaceType.STORE,
                ItemCategory.SWEETS, ItemCategory.DRINKS));
        places.add(new PlaceTag("market", "שוק", "שווקים", MALE, PlaceType.STORE,
                ItemCategory.PRODUCE, ItemCategory.BAKED_GOODS, ItemCategory.SWEETS, ItemCategory.COLLECTIBLE, ItemCategory.CLOTHING, ItemCategory.HARDWARE));

        // --- חנויות מתמחות ---
        places.add(new PlaceTag("craft_store", "חנות יצירה", "חנויות יצירה", FEMALE, PlaceType.STORE,
                ItemCategory.ART_SUPPLIES)); // מוכרת רק יצירה!
        places.add(new PlaceTag("stationery_store", "חנות כלי כתיבה", "חנויות כלי כתיבה", FEMALE, PlaceType.STORE,
                ItemCategory.STATIONERY, ItemCategory.ART_SUPPLIES)); // מוכרת כלי כתיבה וקצת יצירה
        places.add(new PlaceTag("toy_store", "חנות צעצועים", "חנויות צעצועים", FEMALE, PlaceType.STORE,
                ItemCategory.TOY));
        places.add(new PlaceTag("antique_store", "חנות עתיקות", "חנויות עתיקות", FEMALE, PlaceType.STORE,
                ItemCategory.ANTIQUE));
        places.add(new PlaceTag("clothing_store", "חנות בגדים", "חנויות בגדים", FEMALE, PlaceType.STORE,
                ItemCategory.CLOTHING));
        places.add(new PlaceTag("electronics_store", "חנות אלקטרוניקה", "חנויות אלקטרוניקה", FEMALE, PlaceType.STORE,
                ItemCategory.ELECTRONICS));
        places.add(new PlaceTag("hardware_store", "טמבורייה", "טמבוריות", FEMALE, PlaceType.STORE,
                ItemCategory.HARDWARE)); // הבית החדש של החבלים והצינורות!

        // --- בריאות ---
        places.add(new PlaceTag("pharmacy", "בית מרקחת", "בתי מרקחת", MALE, PlaceType.HEALTH,
                ItemCategory.MEDICAL));
        places.add(new PlaceTag("hospital", "בית חולים", "בתי חולים", MALE, PlaceType.HEALTH,
                ItemCategory.MEDICAL, ItemCategory.DRINKS, ItemCategory.BAKED_GOODS)); // קפיטריה בבית חולים

        // --- חינוך (מקומות שבהם משתמשים בציוד, לא קונים אותו) ---
        places.add(new PlaceTag("school", "בית ספר", "בתי ספר", MALE, PlaceType.EDUCATION,
                ItemCategory.STATIONERY, ItemCategory.ART_SUPPLIES));
        places.add(new PlaceTag("library", "ספרייה", "ספריות", FEMALE, PlaceType.EDUCATION,
                ItemCategory.STATIONERY));

        // --- פנאי ותחבורה (מקומות שבהם קונים חטיפים) ---
        places.add(new PlaceTag("cinema", "קולנוע", "בתי קולנוע", MALE, PlaceType.ENTERTAINMENT,
                ItemCategory.SWEETS, ItemCategory.DRINKS));
        places.add(new PlaceTag("airport", "שדה תעופה", "שדות תעופה", MALE, PlaceType.TRANSPORTATION,
                ItemCategory.SWEETS, ItemCategory.DRINKS, ItemCategory.ELECTRONICS, ItemCategory.CLOTHING)); // בדיוטי פרי

        // ==========================================
        // מגורים (HOME)
        // ==========================================
        places.add(new PlaceTag("house", "בית", "בתים", MALE, PlaceType.HOME, ItemCategory.GENERAL_FOOD, ItemCategory.CLOTHING, ItemCategory.ELECTRONICS, ItemCategory.TOY));
        places.add(new PlaceTag("apartment", "דירה", "דירות", FEMALE, PlaceType.HOME, ItemCategory.GENERAL_FOOD, ItemCategory.CLOTHING, ItemCategory.ELECTRONICS, ItemCategory.TOY));

        // ==========================================
        // מרחב ציבורי וטבע (PUBLIC & OUTDOORS)
        // ==========================================
        // ברחוב ובכיכר הגיוני למצוא דברים שנפלו לאנשים: כלי כתיבה, או פריטי לבוש (כמו כובע)
        places.add(new PlaceTag("street", "רחוב", "רחובות", MALE, PlaceType.PUBLIC, ItemCategory.CLOTHING, ItemCategory.STATIONERY));
        places.add(new PlaceTag("square", "כיכר", "כיכרות", FEMALE, PlaceType.PUBLIC, ItemCategory.CLOTHING, ItemCategory.STATIONERY));
        places.add(new PlaceTag("park", "פארק", "פארקים", MALE, PlaceType.OUTDOORS, ItemCategory.TOY, ItemCategory.SWEETS, ItemCategory.CLOTHING));
        places.add(new PlaceTag("forest", "יער", "יערות", MALE, PlaceType.OUTDOORS, ItemCategory.HARDWARE, ItemCategory.CLOTHING));
        places.add(new PlaceTag("beach", "חוף ים", "חופי ים", MALE, PlaceType.OUTDOORS, ItemCategory.TOY, ItemCategory.CLOTHING, ItemCategory.DRINKS));

        // --- מסעדות ומזון (FOOD_SERVICE) ---
        places.add(new PlaceTag("restaurant", "מסעדה", "מסעדות", FEMALE, PlaceType.FOOD_SERVICE, ItemCategory.PRODUCE, ItemCategory.DRINKS, ItemCategory.SWEETS, ItemCategory.BAKED_GOODS));
        places.add(new PlaceTag("cafe", "בית קפה", "בתי קפה", MALE, PlaceType.FOOD_SERVICE, ItemCategory.BAKED_GOODS, ItemCategory.DRINKS, ItemCategory.SWEETS));
        places.add(new PlaceTag("pizzeria", "פיצרייה", "פיצריות", FEMALE, PlaceType.FOOD_SERVICE, ItemCategory.DRINKS));

// --- מרכזי קניות ---
        places.add(new PlaceTag("mall", "קניון", "קניונים", MALE, PlaceType.STORE, ItemCategory.CLOTHING, ItemCategory.ELECTRONICS, ItemCategory.TOY, ItemCategory.SWEETS, ItemCategory.DRINKS));

// --- חינוך ואקדמיה ---
        places.add(new PlaceTag("university", "אוניברסיטה", "אוניברסיטאות", FEMALE, PlaceType.EDUCATION, ItemCategory.STATIONERY, ItemCategory.ELECTRONICS));
        places.add(new PlaceTag("classroom", "כיתה", "כיתות", FEMALE, PlaceType.EDUCATION, ItemCategory.STATIONERY, ItemCategory.ART_SUPPLIES));

// --- בידור, פנאי וספורט ---
        places.add(new PlaceTag("museum", "מוזיאון", "מוזיאונים", MALE, PlaceType.ENTERTAINMENT, ItemCategory.ANTIQUE, ItemCategory.STATIONERY));
        places.add(new PlaceTag("zoo", "גן חיות", "גני חיות", MALE, PlaceType.ENTERTAINMENT, ItemCategory.SWEETS, ItemCategory.DRINKS));
        places.add(new PlaceTag("amusement_park", "פארק שעשועים", "פארקי שעשועים", MALE, PlaceType.ENTERTAINMENT, ItemCategory.SWEETS, ItemCategory.DRINKS, ItemCategory.TOY));
        places.add(new PlaceTag("pool", "בריכה", "בריכות", FEMALE, PlaceType.ENTERTAINMENT, ItemCategory.SWEETS, ItemCategory.DRINKS, ItemCategory.CLOTHING));

// --- תחבורה ---
        places.add(new PlaceTag("bus_station", "תחנת אוטובוס", "תחנות אוטובוס", FEMALE, PlaceType.TRANSPORTATION, ItemCategory.DRINKS, ItemCategory.SWEETS));
        places.add(new PlaceTag("train_station", "תחנת רכבת", "תחנות רכבת", FEMALE, PlaceType.TRANSPORTATION, ItemCategory.DRINKS, ItemCategory.SWEETS, ItemCategory.BAKED_GOODS));

// --- בריאות ---
        places.add(new PlaceTag("clinic", "מרפאה", "מרפאות", FEMALE, PlaceType.HEALTH, ItemCategory.MEDICAL));

        return places;
    }

    public static List<AdjectiveTag> fillAdjectives() {
        List<AdjectiveTag>  adjectives = new ArrayList<>();
        // --- צבעים ---
        AdjectiveTag red = new AdjectiveTag("red", AdjectiveType.COLOR);
        red.addForm(MALE, "s", "אדום");
        red.addForm(FEMALE, "s", "אדומה");
        red.addForm(MALE, "p", "אדומים");
        red.addForm(FEMALE, "p", "אדומות");
        adjectives.add(red);

        AdjectiveTag blue = new AdjectiveTag("blue", AdjectiveType.COLOR);
        blue.addForm(MALE, "s", "כחול");
        blue.addForm(FEMALE, "s", "כחולה");
        blue.addForm(MALE, "p", "כחולים");
        blue.addForm(FEMALE, "p", "כחולות");
        adjectives.add(blue);

        // --- גדלים ---
        AdjectiveTag big = new AdjectiveTag("big", AdjectiveType.SIZE);
        big.addForm(MALE, "s", "גדול");
        big.addForm(FEMALE, "s", "גדולה");
        big.addForm(MALE, "p", "גדולים");
        big.addForm(FEMALE, "p", "גדולות");
        adjectives.add(big);

        AdjectiveTag small = new AdjectiveTag("small", AdjectiveType.SIZE);
        small.addForm(MALE, "s", "קטן");
        small.addForm(FEMALE, "s", "קטנה");
        small.addForm(MALE, "p", "קטנים");
        small.addForm(FEMALE, "p", "קטנות");
        adjectives.add(small);

        // --- מצבים (CONDITION) ---
        AdjectiveTag newAdj = new AdjectiveTag("new", AdjectiveType.CONDITION);
        newAdj.addForm(MALE, "s", "חדש");
        newAdj.addForm(FEMALE, "s", "חדשה");
        newAdj.addForm(MALE, "p", "חדשים");
        newAdj.addForm(FEMALE, "p", "חדשות");
        adjectives.add(newAdj);

        AdjectiveTag oldAdj = new AdjectiveTag("old", AdjectiveType.CONDITION);
        oldAdj.addForm(MALE, "s", "ישן");
        oldAdj.addForm(FEMALE, "s", "ישנה");
        oldAdj.addForm(MALE, "p", "ישנים");
        oldAdj.addForm(FEMALE, "p", "ישנות");
        adjectives.add(oldAdj);

        // הוספה: שבור (מתאים לחומרי בניין, צעצועים, אלקטרוניקה)
        AdjectiveTag broken = new AdjectiveTag("broken", AdjectiveType.CONDITION);
        broken.addForm(MALE, "s", "שבור");
        broken.addForm(FEMALE, "s", "שבורה");
        broken.addForm(MALE, "p", "שבורים");
        broken.addForm(FEMALE, "p", "שבורות");
        adjectives.add(broken);

        // הוספה: טרי (מתאים לפירות, ירקות ומאפים)
        AdjectiveTag fresh = new AdjectiveTag("fresh", AdjectiveType.CONDITION);
        fresh.addForm(MALE, "s", "טרי");
        fresh.addForm(FEMALE, "s", "טרייה");
        fresh.addForm(MALE, "p", "טריים");
        fresh.addForm(FEMALE, "p", "טריות");
        adjectives.add(fresh);

        // --- תחושות/טעם (FEELING) ---
        AdjectiveTag tasty = new AdjectiveTag("tasty", AdjectiveType.FEELING);
        tasty.addForm(MALE, "s", "טעים");
        tasty.addForm(FEMALE, "s", "טעימה");
        tasty.addForm(MALE, "p", "טעימים");
        tasty.addForm(FEMALE, "p", "טעימות");
        adjectives.add(tasty);

        AdjectiveTag identical = new AdjectiveTag("identical", AdjectiveType.CONDITION);
        identical.addForm(Gender.MALE, "s", "זהה נוסף");
        identical.addForm(Gender.FEMALE, "s", "זהה נוספת");
        identical.addForm(Gender.MALE, "p", "זהים נוספים");
        identical.addForm(Gender.FEMALE, "p", "זהות נוספות");
        adjectives.add(identical);

        return  adjectives;
    }

    public static List<UnitTag> fillUnits() {
        List<UnitTag> units = new ArrayList<>();
        // משקל ואורך
        units.add(new UnitTag("kg", "קילוגרם", "קילוגרמים", MALE, UnitType.WEIGHT, ItemCategory.PRODUCE, ItemCategory.GENERAL_FOOD, ItemCategory.ART_SUPPLIES));
        units.add(new UnitTag("meter", "מטר", "מטרים", MALE, UnitType.LENGTH, ItemCategory.HARDWARE));
        units.add(new UnitTag("roll", "גליל", "גלילים", MALE, UnitType.LENGTH, ItemCategory.HARDWARE));

        // נפח
        units.add(new UnitTag("liter", "ליטר", "ליטרים", MALE, UnitType.VOLUME, ItemCategory.DRINKS));
        units.add(new UnitTag("bottle", "בקבוק", "בקבוקים", MALE, UnitType.VOLUME, ItemCategory.DRINKS));

        // ספירה מיוחדת
        units.add(new UnitTag("box", "מארז", "מארזים", MALE, UnitType.COUNT, ItemCategory.SWEETS, ItemCategory.TOY, ItemCategory.STATIONERY, ItemCategory.ELECTRONICS, ItemCategory.MEDICAL, ItemCategory.ART_SUPPLIES));
        units.add(new UnitTag("pack", "חבילה", "חבילות", FEMALE, UnitType.COUNT, ItemCategory.SWEETS, ItemCategory.STATIONERY, ItemCategory.COLLECTIBLE, ItemCategory.MEDICAL, ItemCategory.ART_SUPPLIES));
        units.add(new UnitTag("tray", "מגש", "מגשים", MALE, UnitType.COUNT, ItemCategory.BAKED_GOODS)); // רק מאפים!

        // כלים לסחורה כבדה, חקלאות ועתיקות
        units.add(new UnitTag("sack", "שק", "שקים", MALE, UnitType.COUNT,
                ItemCategory.COLLECTIBLE, ItemCategory.PRODUCE, ItemCategory.GENERAL_FOOD));
        // נמחק ANTIQUE! מטבעות זהב ייכנסו לפה בזכות COLLECTIBLE.

        units.add(new UnitTag("crate", "ארגז", "ארגזים", MALE, UnitType.COUNT,
                ItemCategory.ANTIQUE, ItemCategory.PRODUCE, ItemCategory.DRINKS, ItemCategory.TOY, ItemCategory.HARDWARE));
        // נשאר ANTIQUE! כדי חרס ייכנסו אך ורק לפה.

        units.add(new UnitTag("chest", "תיבה", "תיבות", FEMALE, UnitType.COUNT,
                ItemCategory.COLLECTIBLE));
        // נמחק ANTIQUE!

        // היחידה הריקה (כדי שלא יתווסף "יחידות של" לכל דבר)
        units.add(new UnitTag("none_unit", "", "", MALE, UnitType.NONE,
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
                "grocery", "supermarket", "bakery", "kiosk", "toy_store", "craft_store", "stationery_store",
                "mall", "clothing_store", "electronics_store", "pharmacy", "hardware_store","pizzeria"));

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
                "mall", "clothing_store", "electronics_store", "pharmacy", "hardware_store","pizzeria"));

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

    public static List<VehicleTag> fillVehicles() {
        List<VehicleTag> vehicles = new ArrayList<>();

        vehicles.add(new VehicleTag("car", "מכונית", "מכוניות", FEMALE, VehicleType.CAR));
        vehicles.add(new VehicleTag("truck", "משאית", "משאיות", FEMALE, VehicleType.TRUCK));
        vehicles.add(new VehicleTag("train", "רכבת", "רכבות", FEMALE, VehicleType.TRAIN));
        vehicles.add(new VehicleTag("bus", "אוטובוס", "אוטובוסים", MALE, VehicleType.BUS));
        vehicles.add(new VehicleTag("bicycle", "אופניים", "אופניים", MALE, VehicleType.BICYCLE));
        vehicles.add(new VehicleTag("motorcycle", "אופנוע", "אופנועים", MALE, VehicleType.MOTORCYCLE));
        vehicles.add(new VehicleTag("scooter", "קורקינט", "קורקינטים", MALE, VehicleType.SCOOTER));

        return vehicles;
    }


    public MathQuestion generateForPlayer(RacePlayer player) {
        String level = player.getTrackState().getLevel();
        if (level.isEmpty()) return null;
        return questionEngine.processTemplate(questionTemplateService.getTemplateByDifficulty(level));
    }
}
