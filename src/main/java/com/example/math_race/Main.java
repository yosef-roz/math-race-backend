package com.example.math_race;

import com.example.math_race.questionGenerator.QuestionEngine;
import com.example.math_race.questionGenerator.tags.core.TemplateTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {


    public static void main(String[] args) {

        System.out.println("\n----------------------------------------------------------");
        System.out.println("  Application 'Math Race' is running successfully!");
        System.out.println("----------------------------------------------------------\n");

        Map<String, TemplateTag> memory = new HashMap<>();
        QuestionEngine questionEngine = new QuestionEngine();


        String template1 = "[HUMAN:n:#1] [VERB:id=find;t=past;g=(#1:g);num=s:#V1] [NUM:min=10;max=20:#X] [ITEM:type=COLLECTIBLE:p:#2]. " +
                "[#1:he_she] [VERB:id=give;t=past;g=(#1:g);num=s:#V2] ל[HUMAN:n=!(#1:n):#3] [NUM:min=2;max=5:#Y] [#2:p]. " +
                "כמה [#2:p] נשארו ל[#1]?";

        String template1Result = "[NUM:value=(#X:add_-(#Y)):#R]";

        String c= "[NUM:min=0;max=1:hide:#W] [IF:#W=1:<How many intact [#1:p] remained?>:<How many [#1:p] were destroyed?>]";

        System.out.println("==============================");

        //System.out.println(extractUniqueTags(c));

        List<String[]> templates = new ArrayList<>();

        // 1. חיסור (כמה נשארו)
        templates.add(new String[]{
                "[HUMAN:g=m:#1] [VERB:id=find;t=past;g=(#1:g);num=s:#V1] [NUM:min=10;max=20:#X] [ITEM:type=COLLECTIBLE:p:#2]. " +
                        "[#1:he_she] [VERB:id=give;t=past;g=(#1:g);num=s:#V2] ל[HUMAN:n=!(#1:n):#3] [NUM:min=2;max=5:#Y] [#2:p]. " +
                        "כמה [#2:p] נשארו ל[#1]?",
                "[NUM:value=(#X:add_-(#Y)):#R]"
        });

        // 2. כפל (ארגזים ומארזים)
        templates.add(new String[]{
                "ב[PLACE:type=FOOD:s:#P1] יש [NUM:min=3;max=6:#X] ארגזים. " +
                        "בכל ארגז יש [NUM:min=5;max=10:#Y] [ITEM:type=FOOD:p:#1]. " +
                        "כמה [#1:p] יש בסך הכל [#P1:in_it]?",
                "[NUM:value=(#X:mul_(#Y)):#R]"
        });

        // 3. השוואה (למי יש יותר) - דורש שתי פעולות חיבור
        templates.add(new String[]{
                "ל[HUMAN:n:#1] יש [NUM:min=10;max=30:#X] [ITEM:type=COLLECTIBLE:p:#2]. " +
                        "ל[HUMAN:n=!(#1:n):#3] יש [NUM:min=5;max=15:#Y] [#2:p] יותר מאשר ל[#1]. " +
                        "כמה [#2:p] יש לשניהם ביחד?",
                "[NUM:value=(#X:add_(#X:add_(#Y))):#R]"
        });

        // 4. חילוק (חלוקה שווה)
        templates.add(new String[]{
                "[HUMAN:#1] [VERB:id=buy;g=(#1:g);t=past] [NUM:min=12;max=24:#X] [ITEM:type=STATIONERY:p:#2]. " +
                        "[#1:he_she] רוצה לחלק אותם שווה בשווה בין [NUM:min=2;max=4:#Y] חברות [#1:his_hers]. " +
                        "כמה [#2:p] כל חברה תקבל?",
                "[NUM:value=(#X:div_(#Y)):#R]"
        });

        // 5. קניות ועודף (שילוב כפל וחיסור)
        templates.add(new String[]{
                "[HUMAN:#1] נכנס ל[PLACE:type=TOY;place_type=STORE:s:#P1]. " +
                        "[#1:he_she] [VERB:id=buy;g=(#1:g);t=past] [NUM:min=2;max=4:#X] [ITEM:type=TOY:p:#2], " +
                        "כאשר כל [#2:s] עולה [NUM:min=5;max=10:#Y] שקלים. " +
                        "אם [#1:he_she] שילם בשטר של [NUM:value=50:#Z] שקלים, כמה עודף נשאר?",
                "[NUM:value=(#Z:sub_(#X:mul_(#Y))):#R]"
        });

        templates.add(new String[]{
                "[HUMAN:#1] [VERB:id=find;g=(#1:g);t=past] [NUM:min=5;max=15:#X] [ITEM:type=COLLECTIBLE:p:#2] ב[PLACE:place_type=OUTDOORS:s:#P1]. " +
                        "גם [HUMAN:n=!(#1:n):#3] [VERB:id=find;g=(#3:g);t=past] [NUM:min=5;max=15:#Y] [#2:p] [#P1:in_it]. " +
                        "הם החליטו לחלק את כל ה[#2:p] שווה בשווה בין [NUM:min=2;max=4:#Z] חברים. " +
                        "כמה [#2:p] קיבל כל חבר?",
                "[NUM:value=(#X:add_(#Y)):div_(#Z):#R]"
        });

        templates.add(new String[]{
                "ל[HUMAN:#1] יש [NUM:min=3;max=5:#X] קופסאות של [ITEM:type=FOOD|TOY:p:#2]. " +
                        "בכל קופסה יש [NUM:min=6;max=10:#Y] [#2:p]. " +
                        "[#1:he_she] [VERB:id=give;g=(#1:g);t=past] ל[HUMAN:n=!(#1:n):#3] [NUM:min=5;max=10:#Z] [#2:p]. " +
                        "כמה [#2:p] נשארו ל[#1]?",
                "[NUM:value=(#X:mul_(#Y)):sub_(#Z):#R]"
        });

        templates.add(new String[]{
                "ל[HUMAN:g=f:#1] יש [NUM:min=20;max=40:#X] [ITEM:type=STATIONERY:p:#2]. " +
                        "ל[HUMAN:g=m:#3] יש [NUM:min=5;max=15:#Y] [#2:p] **פחות** מאשר ל[#1]. " +
                        "כמה [#2:p] יש ל[#1] ול[#3] ביחד?",
                "[NUM:value=(#X:add_(#X:sub_(#Y))):#R]"
        });

        templates.add(new String[]{
                "[HUMAN:#1] [VERB:id=enter;g=(#1:g);t=past] ל[PLACE:place_type=STORE:s:#P1]. " +
                        "[#1:he_she] [VERB:id=buy;g=(#1:g);t=past] [NUM:min=2;max=3:#X] [ITEM:type=(#P1:t):p:#2] שעולים [NUM:min=4;max=6:#Y] שקלים כל [#2:one], " +
                        "וגם [ITEM:type=(#P1:t):s:#3] [#3:one] שעולה [NUM:min=10;max=15:#W] שקלים. " +
                        "אם [#1:he_she] נתן שטר של [NUM:value=50:#Z] שקלים, כמה עודף [#1:he_she] יקבל?",
                "[NUM:value=(#Z:sub_(#X:mul_(#Y))):sub_(#W):#R]"
        });

        templates.add(new String[]{
                "ב[PLACE:place_type=HOME:s:#P1] היו [NUM:min=20;max=50:#X] [ITEM:type=MONEY:p:#2]. " +
                        "חילקו את ה[#2:p] שווה בשווה בין [NUM:min=2;max=5:#Y] ילדים. " +
                        "[HUMAN:#3], [#3:one] הילדים, [VERB:id=lose;g=(#3:g);t=past] [NUM:min=1;max=3:#Z] מה[#2:p] [#3:his_hers]. " +
                        "כמה [#2:p] נשארו ל[#3]?",
                "[NUM:value=(#X:div_(#Y)):sub_(#Z):#R]"
        });
        //
        templates.add(new String[]{
                "[HUMAN:#1] [#1:loves] מאוד [ITEM:type=FOOD:p:#2]. [#1:he_she] [VERB:id=eat;g=(#1:g);t=past] [NUM:min=2;max=5:#X] [#2:p] בכל יום במשך [NUM:min=3;max=7:#Y] ימים ברצף. " +
                        "לאחר מכן, [#1:he_she] [VERB:id=eat;g=(#1:g);t=past] עוד [NUM:min=1;max=4:#Z] [#2:p] ביום האחרון. " +
                        "כמה [#2:p] סך הכל [#1:he_she] [VERB:id=eat;g=(#1:g);t=past]?",
                "[NUM:value=(#X:mul_(#Y)):add_(#Z):#R]"
        });

        templates.add(new String[]{
                "ב[PLACE:place_type=STORE:s:#P1] מסדרים מחדש את המלאי. " +
                        "יש שם [NUM:min=8;max=15:#X] מדפים, ועל כל מדף שמו [NUM:min=6;max=12:#Y] [ITEM:type=(#P1:t):p:#1]. " +
                        "לפתע [NUM:min=2;max=(#X:v):#Z] מדפים נשברו וכל ה[#1:p] שעליהם נהרסו. " +
                        "[NUM:min=0;max=1:*:#5]" +
                        "[IF:#5=0:<" +
                        "כמה [#1:p] שלמים נשארו [#P1:in_it]?" +
                        ">:<" +
                        "כמה [#1:p] נהרסו [#P1:in_it]?" +
                        ">]",

                "[IF:#5=0:<" +
                        "[NUM:value=(#X:sub_(#Z)):mul_(#Y):#R]" +
                        ">:<" +
                        "[NUM:value=(#Z):mul_(#Y):#R]" +
                        ">]"
        });

        templates.add(new String[]{
                // --- חלק 1: בניית הסיפור ---
                "[HUMAN:#1] [VERB:id=enter;t=past;g=(#1:g);num=s] ל[PLACE:place_type=STORE;type=FOOD|STATIONERY:s:#P1] בשעה [TIME:min=08.00;max=11.00:#T1] " +
                        "כדי [VERB:id=buy;f=inf] [ITEM:type=(#P1:t);unit_type=WEIGHT|COUNT:p:#2] [ADJ:id=new;g=(#2:g);num=p:#A1]. " +

                        // כאן קורה הקסם: היחידה (U1) נבחרת לפי מה שהפריט (#2) מרשה!
                        "[#1:he_she] [VERB:id=buy;t=past;g=(#1:g);num=s] [NUM:min=3;max=8:#X] [UNIT:type=(#2:allowed_unit):p:#U1] של [#2:p] [#A1]. " +

                        "כל [#U1:s] עולה [NUM:min=5;max=15:#Y] שקלים. " +
                        "תהליך הקנייה לקחה [#1:to_him_her] בדיוק [NUM:min=15;max=45:#M] דקות. " +

                        // --- חלק 2: פיצול השאלה (משתנה נסתר W) ---
                        "[NUM:min=0;max=1:*:#W]" +
                        "[IF:#W=0:<" +
                        "אם [#1:he_she] שילם בקופה בשטר של [NUM:value=200:#Z] שקלים, כמה עודף [#1:he_she] [VERB:id=receive;t=past;g=(#1:g);num=s]?" +
                        ">:<" +
                        "באיזו שעה [#1:he_she] [VERB:id=finish;t=past;g=(#1:g);num=s] את הקנייה ו[VERB:id=take;t=past;g=(#1:g);num=s] את ה[#2:p]?" +
                        ">]",

                // --- חלק 3: פיצול התשובה (חייב להתאים ל-W) ---
                "[IF:#W=0:<" +
                        "[NUM:value=(#Z:sub_(#X:mul_(#Y))):#R]" + // חישוב עודף
                        ">:<" +
                        "[TIME:value=(#T1:add_m_(#M)):#R]" +     // חישוב זמן סיום
                        ">]"
        });


        templates.add(new String[]{
                // 1. הגרלות נסתרות (מגדר מוכר, מקום, והתפקיד עצמו)
                "[NUM:min=0;max=1:*:#OG]" +
                        "[PLACE:place_type=STORE:*:#P1]" +
                        "[ROLE:place_id=(#P1:id);role_type=OPERATOR:*:#O1]" + // יצירת המוכר בזיכרון בלי להדפיס

                        // 2. הסיפור
                        "[HUMAN:#1] [VERB:id=enter;t=past;g=(#1:g);num=s] ל[#P1:s]. " +
                        "ה[IF:#OG=0:<[#O1:m_s] [VERB:id=offer;t=past;g=MALE;num=s]>:<[#O1:f_s] [VERB:id=offer;t=past;g=FEMALE;num=s]>] " +
                        "[#1:to_him_her] [VERB:id=buy;f=inf] [ITEM:type=(#P1:t):p:#2] [ADJ:id=new;g=(#2:g);num=p:#A1]. " +

                        "[#1:n] [VERB:id=want;t=past;g=(#1:g);num=s] [NUM:min=2;max=8:#X] " +
                        // טיפול ברווחים: הרווח נמצא בתוך ה-IF כדי שלא יהיה רווח כפול כשאין יחידה
                        "[UNIT:type=(#2:allowed_unit);item_category=(#2:t):p:#U1][IF:#2:allowed_unit=NONE:<>:< של>] [#2:p] [#A1]. " +

                        "המחיר עבור כל [IF:#2:allowed_unit=NONE:<[#2:s]>:<[#U1:s]>] הוא [NUM:min=5;max=20:#Y] שקלים. " +

                        // 3. השאלה
                        "[NUM:min=0;max=1:*:#W]" +
                        "[IF:#W=0:<" +
                        "כמה שקלים סך הכל [#1:he_she] [VERB:id=need;t=past;g=(#1:g);num=s] [VERB:id=pay;f=inf]?" +
                        ">:<" +
                        "[#1:n] [VERB:id=pay;t=past;g=(#1:g);num=s] בשטר של [NUM:value=200:#Z] שקלים. " +
                        "כמה עודף [#1:he_she] [VERB:id=receive;t=past;g=(#1:g);num=s] מה[IF:#OG=0:<[#O1:m_s]>:<[#O1:f_s]>]?" +
                        ">]",

                // 4. תשובה
                "[IF:#W=0:<[NUM:value=(#X:mul_(#Y)):#R]>:<[NUM:value=(#Z:sub_(#X:mul_(#Y))):#R]>]"
        });

        // מכאן

        templates.add(new String[]{
                // 1. הגרלות נסתרות (מקום, מוכר, פריט שמוכרים בחנות הזו)
                "[NUM:min=0;max=1:*:#OG]" +
                        "[PLACE:place_type=STORE:*:#P1]" +
                        "[ROLE:place_id=(#P1:id);role_type=OPERATOR:*:#O1]" +
                        "[ITEM:type=(#P1:t):*:#2]" +

                        // 2. הסיפור
                        "[HUMAN:#1] [VERB:id=enter;t=past;g=(#1:g);num=s] ל[#P1:s]. " +
                        "ה[IF:(#OG)=0:<[#O1:m_s] תלה>:<[#O1:f_s] תלתה>] שלט מבצע על ה[#2:p]: " +
                        "\"קנו [NUM:min=3;max=5:#X] [UNIT:type=(#2:allowed_unit):p:#U1][IF:(#2:allowed_unit)=NONE:<>:< של>] [#2:p] [ADJ:id=new;g=(#2:g);num=p:#A1], " +
                        "וקבלו [NUM:min=1;max=2:#B] מתנה!\". " +

                        "[#1:n] [VERB:id=buy;t=past;g=(#1:g);num=s] [NUM:min=2;max=8:#Y] [IF:(#2:allowed_unit)=NONE:<[#2:p]>:<[#U1:p]>]. " +
                        // 3. השאלה (האם מגיע לו בונוס?)
                        "כמה [IF:(#2:allowed_unit)=NONE:<[#2:p]>:<[#U1:p]>] של [#2:p] יש ל[#1] עכשיו בסך הכל?",

                // 4. התשובה (מחשב בונוס רק אם הוא קנה מספיק)
                "[IF:(#Y)>=(#X):<[NUM:value=(#Y:add_(#B)):#R]>:<[NUM:value=(#Y):#R]>]"
        });


        templates.add(new String[]{
                // 1. הגרלות נסתרות
                "[PLACE:place_type=OUTDOORS|HOME:*:#P1]" +
                        "[ITEM:type=(#P1:t):*:#2]" +

                        // 2. הסיפור עם הפיצול הדינמי למקום
                        "[HUMAN:#1] ו[HUMAN:n=!(#1:n):#3] [IF:(#P1:pt)=OUTDOORS:<בילו ב[#P1:s] וחיפשו מציאות>:<[VERB:id=arrange;t=past;g=MALE;num=p] את ה[#P1:s]>]. " +

                        "[#1:n] [VERB:id=find;t=past;g=(#1:g);num=s] [NUM:min=10;max=25:#X] [#2:p] [ADJ:id=old;g=(#2:g);num=p:#A1]. " +
                        "[#3:n] [VERB:id=find;t=past;g=(#3:g);num=s] [NUM:min=10;max=25:#Y] [#2:p] [#A1]. " +

                        "לאחר מכן, הם החליטו לאסוף את הכל יחד ולחלק את כל מה שנמצא שווה בשווה ל-[NUM:min=3;max=6:#Z] קופסאות קטנות. " +

                        // 3. מגרילים איזה סוג שאלה נשאל (0 = שארית, 1 = כמות בכל קופסה)
                        "[NUM:min=0;max=1:*:#W]" +

                        // 4. השאלה - מפוצלת לפי W
                        "[IF:(#W)=0:<" +
                        "כמה [#2:p] [#A1] נשארו בחוץ כי לא התחלקו שווה בשווה בין הקופסאות?" +
                        ">:<" +
                        "כמה [#2:p] [#A1] נכנסו בדיוק לתוך כל קופסה?" +
                        ">]",

                // 5. התשובה - מפוצלת לפי W (חייבת להיות תואמת לשאלה!)
                "[IF:(#W)=0:<" +
                        "[NUM:value=(#X:add_(#Y)):mod_(#Z):#R]" + // חישוב שארית
                        ">:<" +
                        "[NUM:value=(#X:add_(#Y)):div_(#Z):#R]" + // חישוב חלוקה רגילה
                        ">]"
        });

        templates.add(new String[]{
                // 1. הגרלות נסתרות (הפריט מותאם דינמית למקום!)
                "[PLACE:place_type=EDUCATION|HOME:*:#P1]" +
                        "[ITEM:type=(#P1:t):*:#2]" +

                        // 2. הסיפור - נקי לגמרי מתנאי IF מסורבלים בזכות מילון הפעלים החדש
                        "[HUMAN:#1] [VERB:id=sit;t=past;g=(#1:g);num=s] ב[#P1:s] בשעה [TIME:min=14.00;max=16.00:#T1]. " +
                        "[#1:he_she] [VERB:id=can;t=present;g=(#1:g);num=s] [VERB:id=arrange;f=inf] [NUM:min=4;max=8:#X] [#2:p] בכל דקה. " +

                        // 3. הגרלת סוג השאלה: 0 = כמות פריטים, 1 = שעת סיום
                        "[NUM:min=0;max=1:*:#W]" +

                        // 4. השאלה המפוצלת (ללא אות ל' מיותרת לפני פועל המקור)
                        "[IF:(#W)=0:<" +
                        "אם [#1:he_she] [VERB:id=arrange;t=past;g=(#1:g);num=s] [#2:p] ברצף עד השעה [TIME:value=(#T1:add_m_15):#T2], כמה [#2:p] [#1:he_she] [VERB:id=arrange;t=past;g=(#1:g);num=s] סך הכל?" +
                        ">:<" +
                        "אם היו שם [NUM:value=(#X:mul_20):#Y] [#2:p] בסך הכל, באיזו שעה [#1:he_she] [VERB:id=finish;t=past;g=(#1:g);num=s] [VERB:id=arrange;f=inf] את כולם?" +
                        ">]",

                // 5. התשובה המפוצלת (מותאמת בדיוק לשאלה שנבחרה)
                "[IF:(#W)=0:<" +
                        "[NUM:value=(#X:mul_15):#R]" + // חישוב כמות: קצב * 15 דקות
                        ">:<" +
                        "[TIME:value=(#T1:add_m_20):#R]" + // חישוב שעה: מוסיף בדיוק 20 דקות לזמן ההתחלה
                        ">]"
        });

        List<String[]> newTemplates = new ArrayList<>();
//        newTemplates.add(new String[]{
//                "שאלה",
//                "תשובה",
//                "תשובה שגויה 1",
//                "תשובה שגויה 2",
//                "תשובה שגויה 2",
//                "רמז"
//        });
// 1. מהירות ודרך (תנועה) - מספרים דו-ספרתיים קטנים
        newTemplates.add(new String[]{
                "[HUMAN:#1] [VERB:id=walk:(present_+(#1:g)+_s):#V1] ל[PLACE:place_type=OUTDOORS:s:#P1] במהירות [NUM:min=12;max=20:#X] קמ\"ש " +
                        "במשך [NUM:min=3;max=6:#Y] שעות. [NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<כמה ק\"מ [#1:he_she] [VERB:id=walk:(past_+(#1:g)+_s):#V2] סך הכל?>" +
                        ":<אם המהירות הייתה קטנה ב-2 קמ\"ש, כמה ק\"מ [#1:he_she] היה [VERB:id=walk:(present_+(#1:g)+_s):#V3] באותו זמן?>]",

                "[IF:(#W)=0:<[NUM:value=(#X:mul_(#Y)):#R]>:<[NUM:value=(#X:sub_2):mul_(#Y):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#X:add_(#Y)):#R]>:<[NUM:value=(#X:mul_(#Y)):sub_2:#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#X:mul_(#Y)):add_10:#R]>:<[NUM:value=(#X:add_2):mul_(#Y):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#X:mul_(#Y)):sub_10:#R]>:<[NUM:value=(#X:mul_(#Y)):#R]>]",
                "[IF:(#W)=0:<דרך שווה למהירות כפול זמן.>:<קודם מצא את המהירות החדשה (פחות 2), ואז הכפל בזמן.>]"
        });

// 2. גיאומטריה: שטח והיקף של מלבן - מספרים דורשי מחשבה
        newTemplates.add(new String[]{
                "ב[PLACE:place_type=EDUCATION|HOME:s:#P1] יש חדר מלבני. האורך שלו הוא [NUM:min=12;max=25:#L] מטרים " +
                        "והרוחב הוא [NUM:min=4;max=9:#WIDTH] מטרים. [NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<מה שטח החדר במ\"ר?>" +
                        ":<מה היקף החדר במטרים?>]",

                "[IF:(#W)=0:<[NUM:value=(#L:mul_(#WIDTH)):#R]>:<[NUM:value=(#L:add_(#WIDTH)):mul_2:#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#L:add_(#WIDTH)):#R]>:<[NUM:value=(#L:mul_(#WIDTH)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#L:mul_(#WIDTH)):mul_2:#R]>:<[NUM:value=(#L:add_(#WIDTH)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#L:mul_(#WIDTH)):sub_10:#R]>:<[NUM:value=(#L:add_(#WIDTH)):mul_2:add_4:#R]>]",
                "[IF:(#W)=0:<שטח הוא אורך כפול רוחב.>:<היקף הוא סכום כל ארבעת הצלעות (אורך ועוד רוחב, כפול שתיים).>]"
        });

// 3. יחס ופרופורציה - כפולות קצת יותר מאתגרות
        newTemplates.add(new String[]{
                "[HUMAN:#1] [VERB:id=prepare:(present_+(#1:g)+_s):#V1] מתכון. היחס בין כוסות סוכר לכוסות קמח הוא 1 ל-[NUM:min=3;max=6:#RATIO]. " +
                        "[NUM:min=4;max=12:*:#MULT][NUM:value=(#RATIO:mul_(#MULT)):*:#FLOUR][NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<אם [#1:he_she] [VERB:id=put:(past_+(#1:g)+_s):#V2] [#MULT] כוסות סוכר, כמה כוסות קמח צריך?>" +
                        ":<אם [#1:he_she] [VERB:id=put:(past_+(#1:g)+_s):#V3] [#FLOUR] כוסות קמח, כמה כוסות סוכר צריך?>]",

                "[IF:(#W)=0:<[NUM:value=(#FLOUR):#R]>:<[NUM:value=(#MULT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#MULT:add_(#RATIO)):#R]>:<[NUM:value=(#FLOUR:mul_(#RATIO)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#FLOUR:add_2):#R]>:<[NUM:value=(#MULT:add_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#FLOUR:sub_2):#R]>:<[NUM:value=(#MULT:sub_1):#R]>]",
                "[IF:(#W)=0:<על כל כוס סוכר, שמים [#RATIO] כוסות קמח. הכפל ביחס.>:<חלק את כמות הקמח במספר היחס כדי למצוא את הסוכר.>]"
        });

// 4. שברים פשוטים - עבודה עם כמויות גדולות יותר שמתחלקות ב-4
        newTemplates.add(new String[]{
                "[HUMAN:#1] [VERB:id=buy:(past_+(#1:g)+_s):#V1] [NUM:min=10;max=25:*:#BASE][NUM:value=(#BASE:mul_4):#X] [ITEM:type=SWEETS:p:#2]. " +
                        "[#1:he_she] [VERB:id=give:(past_+(#1:g)+_s):#V2] ל[HUMAN:n=!(#1:n):#3] [NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<חצי>:<רבע>] מה[#2:p]. " +
                        "כמה [#2:p] נשארו ל[#1]?",

                "[IF:(#W)=0:<[NUM:value=(#BASE:mul_2):#R]>:<[NUM:value=(#BASE:mul_3):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE):#R]>:<[NUM:value=(#BASE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE:mul_3):#R]>:<[NUM:value=(#BASE:mul_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE:mul_2):add_10:#R]>:<[NUM:value=(#BASE:mul_3):add_5:#R]>]",
                "[IF:(#W)=0:<כדי למצוא כמה נשארו אחרי שנתת חצי, פשוט חלק את הכמות הכוללת ב-2.>:<כדי למצוא כמה נשארו, חלק ל-4, ואז חסר את זה מהכמות הכוללת.>]"
        });

// 5. מציאת נעלם - משתני עזר מובנים ומספרים קלים לחילוק הופכי
        newTemplates.add(new String[]{
                "[HUMAN:#1] [VERB:id=choose:(past_+(#1:g)+_s):#V1] מספר. " +
                        "[NUM:min=6;max=15:*:#ORIG][NUM:min=2;max=4:*:#X][NUM:min=5;max=15:*:#Y]" +
                        "[NUM:value=(#ORIG:mul_(#X)):*:#TEMP1][NUM:value=(#TEMP1:add_(#Y)):*:#RES1]" +
                        "[NUM:value=(#ORIG:add_(#X)):*:#TEMP2][NUM:value=(#TEMP2:sub_(#Y)):*:#RES2]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<[#1:he_she] [VERB:id=multiply:(past_+(#1:g)+_s):#V2] אותו ב-[#X] והוסיף [#Y]. התוצאה היא [#RES1].>" +
                        ":<[#1:he_she] הוסיף לו [#X] וחיסר [#Y]. התוצאה היא [#RES2].>] " +
                        "מה המספר המקורי?",

                "[NUM:value=(#ORIG):#R]",
                "[IF:(#W)=0:<[NUM:value=(#ORIG:mul_2):#R]>:<[NUM:value=(#ORIG:add_1):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#ORIG:sub_1):#R]>:<[NUM:value=(#ORIG:sub_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#ORIG:add_2):#R]>:<[NUM:value=(#ORIG:add_3):#R]>]",
                "[IF:(#W)=0:<בצע את הפעולות ההפוכות מהסוף להתחלה: חסר [#Y] ואז חלק ב-[#X].>:<בצע את הפעולות ההפוכות מהסוף להתחלה: הוסף [#Y] ואז חסר [#X].>]"
        });

// 6. ממוצעים - כפל דו ספרתי בחד ספרתי
        newTemplates.add(new String[]{
                "[HUMAN:#1] [VERB:id=read:(present_+(#1:g)+_s):#V1] ספר. בממוצע [#1:he_she] [VERB:id=read:(past_+(#1:g)+_s):#V2] " +
                        "[NUM:min=15;max=25:#AVG] עמודים ביום במשך [NUM:min=4;max=7:#DAYS] ימים. [NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<כמה עמודים [#1:he_she] [VERB:id=read:(past_+(#1:g)+_s):#V3] סך הכל?>" +
                        ":<אם הממוצע היה גדול ב-2 עמודים ליום, כמה עמודים [#1:he_she] היה [VERB:id=read:(present_+(#1:g)+_s):#V4] סך הכל?>]",

                "[IF:(#W)=0:<[NUM:value=(#AVG:mul_(#DAYS)):#R]>:<[NUM:value=(#AVG:add_2):mul_(#DAYS):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AVG:add_(#DAYS)):#R]>:<[NUM:value=(#AVG:mul_(#DAYS)):add_2:#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AVG:mul_(#DAYS)):add_10:#R]>:<[NUM:value=(#AVG:mul_(#DAYS)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AVG:mul_(#DAYS)):sub_5:#R]>:<[NUM:value=(#AVG:add_1):mul_(#DAYS):#R]>]",
                "[IF:(#W)=0:<סך הכל שווה לממוצע כפול מספר הימים.>:<מצא את הממוצע החדש, ואז הכפל אותו במספר הימים.>]"
        });

// 7. חוקיות וסדרות - דורש חיבור מדורג בראש
        newTemplates.add(new String[]{
                "[HUMAN:#1] [VERB:id=arrange:(present_+(#1:g)+_s):#V1] [ITEM:type=STATIONERY:p:#2] בשורה. " +
                        "בשורה הראשונה יש [NUM:min=5;max=12:#START] [#2:p], ובכל שורה הבאה יש [NUM:min=3;max=7:#JUMP] [#2:p] יותר מאשר בשורה הקודמת. " +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<כמה [#2:p] יש בשורה השלישית?>" +
                        ":<כמה [#2:p] יש בסך הכל ב-3 השורות הראשונות יחד?>]",

                "[IF:(#W)=0:<[NUM:value=(#JUMP:mul_2):add_(#START):#R]>:<[NUM:value=(#START:mul_3):add_(#JUMP:mul_3):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#JUMP:mul_3):add_(#START):#R]>:<[NUM:value=(#START:mul_3):add_(#JUMP:mul_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#START:add_(#JUMP)):#R]>:<[NUM:value=(#JUMP:mul_2):add_(#START):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#JUMP:mul_2):add_(#START):add_2:#R]>:<[NUM:value=(#START:mul_3):add_(#JUMP:mul_3):sub_2:#R]>]",
                "[IF:(#W)=0:<הוסף פעמיים את הגידול לכמות ההתחלתית.>:<חשב את הכמות בכל אחת משלוש השורות, וחבר את הכל יחד.>]"
        });

// 8. תכנון תקציב וחיסכון - מתוקן עם היגיון זמנים נכון
        newTemplates.add(new String[]{
                "ל[HUMAN:#1] יש קופה עם [NUM:min=100;max=250:#TOTAL] שקלים. " +
                        "בכל שבוע [#1:he_she] [VERB:id=save:(present_+(#1:g)+_s):#V1] עוד [NUM:min=20;max=50:#SAVE] שקלים. " +
                        "[NUM:min=3;max=8:*:#M][NUM:value=(#SAVE:mul_(#M)):*:#SPEND][NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<כמה שקלים יהיו [#1:to_him_her] בעוד [NUM:min=4;max=9:#WEEKS] שבועות?>" +
                        ":<אם [#1:he_she] [VERB:id=want:(present_+(#1:g)+_s):#V2] [VERB:id=buy:INF:#TEMP10] משהו שעולה [#SPEND] שקלים (מבלי לגעת בכסף שבקופה), כמה שבועות ייקח לחסוך את הסכום הזה?>]",

                "[IF:(#W)=0:<[NUM:value=(#SAVE:mul_(#WEEKS)):add_(#TOTAL):#R]>:<[NUM:value=(#M):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SAVE:mul_(#WEEKS)):#R]>:<[NUM:value=(#M:add_1):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:mul_(#WEEKS)):#R]>:<[NUM:value=(#M:sub_1):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SAVE:mul_(#WEEKS)):add_(#TOTAL):add_20:#R]>:<[NUM:value=(#M:add_2):#R]>]",
                "[IF:(#W)=0:<הכפל את החיסכון במספר השבועות והוסף לסכום ההתחלתי.>:<חלק את מחיר הפריט בסכום החיסכון השבועי.>]"
        });

// 9. בעיות גיל - סכומים גדולים יותר שמצריכים ריכוז
        newTemplates.add(new String[]{
                "[HUMAN:#1] בן [NUM:min=12;max=16:#AGE1]. [HUMAN:n=!(#1:n):#2] גדול ממנו ב-[NUM:min=4;max=9:#DIFF] שנים. " +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<בן כמה יהיה [#2] בעוד [NUM:min=5;max=10:#YEARS] שנים?>" +
                        ":<מה סכום הגילים שלהם היום?>]",

                "[IF:(#W)=0:<[NUM:value=(#AGE1:add_(#DIFF)):add_(#YEARS):#R]>:<[NUM:value=(#AGE1:mul_2):add_(#DIFF):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AGE1:add_(#YEARS)):#R]>:<[NUM:value=(#AGE1:add_(#DIFF)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AGE1:add_(#DIFF)):sub_(#YEARS):#R]>:<[NUM:value=(#AGE1:mul_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AGE1:add_(#DIFF)):#R]>:<[NUM:value=(#AGE1:mul_2):add_(#DIFF):add_2:#R]>]",
                "[IF:(#W)=0:<מצא את הגיל של [#2] היום, ואז הוסף את השנים שעברו.>:<חבר את הגיל של [#1] לגיל של [#2] (שהוא הגיל של [#1] ועוד ההפרש).>]"
        });

// 10. חישוב מארזים - כפל דו ספרתי בחד ספרתי (גבול עליון של לוח הכפל)
        newTemplates.add(new String[]{
                "[HUMAN:#1] [VERB:id=buy:(past_+(#1:g)+_s)] [NUM:min=5;max=12:#PACKS] מארזים של [ITEM:type=DRINKS:p:#2]. " +
                        "בכל מארז יש [NUM:min=4;max=8:#IN_PACK] [#2:p]. [NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<כמה [#2:p] יש סך הכל ב-[#PACKS] המארזים?>" +
                        ":<אם [#1:he_she] [VERB:id=give:(past_+(#1:g)+_s):#V2] לחברים מארז אחד שלם, כמה [#2:p] נשארו [#1:to_him_her]?>]",

                "[IF:(#W)=0:<[NUM:value=(#PACKS:mul_(#IN_PACK)):#R]>:<[NUM:value=(#PACKS:sub_1):mul_(#IN_PACK):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PACKS:add_(#IN_PACK)):#R]>:<[NUM:value=(#PACKS:mul_(#IN_PACK)):sub_1:#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PACKS:mul_(#IN_PACK)):add_4:#R]>:<[NUM:value=(#PACKS:sub_2):mul_(#IN_PACK):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PACKS:mul_(#IN_PACK)):sub_4:#R]>:<[NUM:value=(#PACKS:mul_(#IN_PACK)):add_(#IN_PACK):#R]>]",
                "[IF:(#W)=0:<הכפל את כמות המארזים בכמות שיש בכל מארז.>:<חסר מארז אחד מסך המארזים, ואז הכפל בכמות שיש בפנים.>]"
        });

        //11 - מפה חדש
        newTemplates.add(new String[]{
                // 0. השאלה + הגרלות נסתרות (זמן ריצה מינימלי)
                "[PLACE:place_type=STORE:*:#P1]" +
                        "[ROLE:place_id=(#P1:id);role_type=OPERATOR:*:#O1]" + // הוספנו: הגרלת בעל התפקיד בחנות
                        "[ITEM:type=(#P1:t):*:#I1]" +
                        "[ITEM:type=TOY|SWEETS:*:#I2]" +
                        "[HUMAN:*:#1]" +

                        // הגרלות מספרים (חישוב מהיר)
                        "[NUM:min=3;max=6:*:#QTY]" +
                        "[NUM:min=5;max=12:*:#PRICE]" +
                        "[NUM:value=100:*:#BILL]" +
                        "[NUM:min=10;max=20:*:#EXTRA_PRICE]" +
                        "[NUM:value=(#QTY:mul_(#PRICE)):*:#TOTAL_COST]" +

                        // פיצול עלילה (0/1) והגרלת מגדר בעל התפקיד (0=זכר, 1=נקבה)
                        "[NUM:min=0;max=1:*:#W]" +
                        "[NUM:min=0;max=1:*:#OG]" +

                        // הסיפור הבסיסי
                        "[#1:n] [VERB:id=buy:(past_+(#1:g)+_s)] ב[#P1:s] [#QTY] [#I1:p]. כל [#I1:s] עולה [#PRICE] שקלים. " +

                        // פיצול השאלה עם השילוב של בעל התפקיד
                        "[IF:(#W)=0:<" +
                        "אם [#1:he_she] [VERB:id=pay:(past_+(#1:g)+_s)] [IF:(#OG)=0:<ל[#O1:sm]>:<ל[#O1:sf]>] בשטר של [#BILL] שקלים, כמה עודף [#1:he_she] [VERB:id=receive:(past_+(#1:g)+_s)]?" +
                        ">:<" +
                        "בנוסף, [#1:he_she] [VERB:id=buy:(past_+(#1:g)+_s)] מה[IF:(#OG)=0:<[#O1:sm]>:<[#O1:sf]>] גם [#I2:s] ב-[#EXTRA_PRICE] שקלים. כמה שקלים סך הכל [#1:he_she] [VERB:id=pay:(past_+(#1:g)+_s)]?" +
                        ">]",

                // 1. התשובה הנכונה
                "[IF:(#W)=0:<[NUM:value=(#BILL:sub_(#TOTAL_COST)):#R]>:<[NUM:value=(#TOTAL_COST:add_(#EXTRA_PRICE)):#R]>]",

                // 2. מסיח 1: טעות בפעולה החשבונית
                "[IF:(#W)=0:<[NUM:value=(#BILL:sub_(#PRICE)):#R]>:<[NUM:value=(#PRICE:add_(#EXTRA_PRICE)):#R]>]",

                // 3. מסיח 2: תשובה חלקית
                "[NUM:value=(#TOTAL_COST):#R]",

                // 4. מסיח 3: התבלבל במספרים
                "[IF:(#W)=0:<[NUM:value=(#BILL:sub_(#QTY)):#R]>:<[NUM:value=(#QTY:add_(#EXTRA_PRICE)):#R]>]",

                // 5. רמז
                "[IF:(#W)=0:<" +
                        "כדי למצוא את העודף, חשב קודם כמה עולים ה-[#I1:p] יחד (כמות כפול מחיר), וחסר את התוצאה מ-100." +
                        ">:<" +
                        "חשב קודם את מחיר ה-[#I1:p] יחד (כמות כפול מחיר), ואז חבר לסכום את מחיר ה-[#I2:s]." +
                        ">]"
        });

        newTemplates.add(new String[]{
                // 0. השאלה + הגרלות נסתרות (זמן ריצה מינימלי)
                "[PLACE:place_type=HOME|EDUCATION:*:#P1]" +
                        "[ITEM:type=(#P1:t);unit_type=COUNT:*:#I1]" +
                        "[UNIT:type=(#I1:allowed_unit);item_category=(#I1:t):*:#U1]" +
                        "[HUMAN:*:#1]" +
                        "[HUMAN:n=!(#1:n):*:#2]" + // חבר שאליו נותנים את הפריטים

                        // הנדסה לאחור של המספרים כדי להבטיח חילוק שלם תמיד!
                        "[NUM:min=3;max=5:*:#BOXES]" +          // כמות הקופסאות/מארזים (מחלק קטן וקל)
                        "[NUM:min=4;max=9:*:#IN_BOX]" +         // התוצאה הסופית - כמות בכל קופסה
                        "[NUM:value=(#BOXES:mul_(#IN_BOX)):*:#TARGET]" + // המספר שיתחלק בשלמות (למשל 20)
                        "[NUM:min=2;max=8:*:#DIFF]" +           // המספר שמחברים או מחסרים בהתחלה
                        "[NUM:value=(#TARGET:add_(#DIFF)):*:#START_SUB]" + // סכום התחלתי לעלילת חיסור
                        "[NUM:value=(#TARGET:sub_(#DIFF)):*:#START_ADD]" + // סכום התחלתי לעלילת חיבור

                        // פיצול עלילה
                        "[NUM:min=0;max=1:*:#W]" +

                        // פיצול השאלה (טקסט קצר, קולע וממוקד)
                        "[IF:(#W)=0:<" +
                        "ל[#1:n] היו [#START_SUB] [#I1:p]. [#1:he_she] [VERB:id=give:(past_+(#1:g)+_s)] ל[#2:n] [#DIFF] [#I1:p], ואת השאר [VERB:id=divide:(past_+(#1:g)+_s)] שווה בשווה ל-[#BOXES] [#U1:p]. כמה [#I1:p] יש בכל [#U1:s]?" +
                        ">:<" +
                        "ל[#1:n] היו [#START_ADD] [#I1:p]. [#1:he_she] [VERB:id=find:(past_+(#1:g)+_s)] עוד [#DIFF] [#I1:p], ואז [VERB:id=divide:(past_+(#1:g)+_s)] הכל שווה בשווה ל-[#BOXES] [#U1:p]. כמה [#I1:p] יש בכל [#U1:s]?" +
                        ">]",

                // 1. התשובה הנכונה (היא תמיד המספר שקבענו מראש!)
                "[NUM:value=(#IN_BOX):#R]",

                // 2. מסיח 1: שכח לחלק (ביצע רק את פעולת החיבור/חיסור ועצר)
                "[NUM:value=(#TARGET):#R]",

                // 3. מסיח 2: כפל במקום לחלק (טעות נפוצה בלחץ זמן)
                "[NUM:value=(#TARGET:mul_(#BOXES)):#R]",

                // 4. מסיח 3: חיסר את כמות הקופסאות במקום לחלק בהן
                "[NUM:value=(#TARGET:sub_(#BOXES)):#R]",

                // 5. רמז
                "[IF:(#W)=0:<" +
                        "כדי לפתור, קודם חסר את ה-[#I1:p] ש[#1:n] [VERB:id=give:(past_+(#1:g)+_s)], ואז חלק את התוצאה ב-[#BOXES]." +
                        ">:<" +
                        "כדי לפתור, קודם חבר את ה-[#I1:p] ש[#1:n] [VERB:id=find:(past_+(#1:g)+_s)], ואז חלק את התוצאה ב-[#BOXES]." +
                        ">]"
        });

        newTemplates.add(new String[]{
                // 0. הגרלות נסתרות (Setup)
                "[PLACE:place_type=STORE|FOOD_SERVICE:*:#P1]" +
                        "[ROLE:place_id=(#P1:id);role_type=OPERATOR:*:#O1]" +
                        "[ITEM:type=(#P1:t);unit_type=COUNT:*:#I1]" +
                        "[HUMAN:*:*:#1]" + // הגרלה רנדומלית לחלוטין

                        // מספרים קטנים לחישוב מהיר (15 שניות)
                        "[NUM:min=8;max=12:*:#MINS_PER]" +
                        "[NUM:min=3;max=6:*:#QTY]" +
                        "[NUM:value=(#QTY:mul_(#MINS_PER)):*:#TOTAL_MINS]" +
                        "[TIME:min=08.00;max=16.00;round=true:*:#T_START]" +

                        "[NUM:min=0;max=1:*:#W]" +

                        // בניית הסיפור - נקי ודינמי בזכות [#O1:s(#1:g)]
                        "[#1:n] ה[#O1:s(#1:g)] [VERB:id=prepare:(past_+(#1:g)+_s):#V1] [#I1:p] ב[#P1:s]. " +
                        "הכנת כל [#I1:s] לוקחת בדיוק [#MINS_PER] דקות. " +

                        // פיצול השאלה
                        "[IF:(#W)=0:<" +
                        "אם [#1:he_she] [VERB:id=start:(past_+(#1:g)+_s):#V2] להכין [#QTY] [#I1:p] בשעה [#T_START], באיזו שעה [#1:he_she] [VERB:id=finish:(past_+(#1:g)+_s):#V3]?" +
                        ">:<" +
                        "אם [#1:he_she] [VERB:id=work:(past_+(#1:g)+_s):#V4] ברצף במשך [#TOTAL_MINS] דקות, כמה [#I1:p] [#1:he_she] [VERB:id=prepare:(past_+(#1:g)+_s):#V5] סך הכל?" +
                        ">]",

                // 1. התשובה הנכונה
                "[IF:(#W)=0:<[TIME:value=(#T_START:add_m_(#TOTAL_MINS)):#R]>:<[NUM:value=(#TOTAL_MINS:div_(#MINS_PER)):#R]>]",

                // 2. מסיחים (כולל השרשור האלגנטי של הטעות בזמנים)
                "[IF:(#W)=0:<[TIME:value=(#T_START:add_m_(#MINS_PER)):#R]>:<[NUM:value=(#TOTAL_MINS:mul_(#MINS_PER)):#R]>]",
                "[IF:(#W)=0:<[TIME:value=(#T_START:add_m_(#QTY)):#R]>:<[NUM:value=(#TOTAL_MINS:sub_(#MINS_PER)):#R]>]",
                "[IF:(#W)=0:<[TIME:value=(#T_START:add_m_(#TOTAL_MINS)):add_m_10:#R]>:<[NUM:value=(#QTY:add_1):#R]>]",

                // 5. רמז
                "[IF:(#W)=0:<" +
                        "כדי לפתור, חשב כמה דקות לוקח להכין את כל ה-[#I1:p] יחד (כפל), ואז הוסף את התוצאה לשעת ההתחלה." +
                        ">:<" +
                        "כדי למצוא את הכמות, חלק את סך כל דקות העבודה ([#TOTAL_MINS]) בזמן שלוקח להכין [#I1:s] אחד ([#MINS_PER])." +
                        ">]"
        });


        // הרצה של המנוע
        System.out.println("--- מריץ ייצור שאלות ---");

        for (int i = 0; i < newTemplates.size(); i++) {
            memory = new HashMap<>(); // זיכרון נקי לכל שאלה
            String[] pair = newTemplates.get(i);

            String question = questionEngine.evaluateTemplate(pair[0], memory);
            String answer = questionEngine.evaluateTemplate(pair[1], memory);
            String w1answer = questionEngine.evaluateTemplate(pair[2], memory);
            String w2answer = questionEngine.evaluateTemplate(pair[3], memory);
            String w3answer = questionEngine.evaluateTemplate(pair[4], memory);
            String hint = questionEngine.evaluateTemplate(pair[5], memory);

            System.out.println("שאלה " + (i+1) + ": " + question);
            System.out.println("תשובה נכונה: " + answer);
            System.out.println("תשובה שגויה 1 : " + w1answer);
            System.out.println("תשובה שגויה 2 : " + w2answer);
            System.out.println("תשובה שגויה 3 : " + w3answer);
            System.out.println("רמז : " + hint);
            System.out.println("-------------------------");
        }
    }
}
