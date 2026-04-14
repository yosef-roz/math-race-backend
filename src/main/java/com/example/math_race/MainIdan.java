package com.example.math_race;


import com.example.math_race.questionGenerator.QuestionEngine;
import com.example.math_race.questionGenerator.tags.core.TemplateTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.math_race.questionGenerator.QuestionEngine;
import com.example.math_race.questionGenerator.tags.core.TemplateTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainIdan {
    
    public static void main(String[] args) {

        //[HUMAN:g=m:#1] [VERB:id=buy:(past_+(#1:g)+_s)]
        // past_male_p/s
        String tem = "[HUMAN:n:#n] [VERB:id=buy:(past_+(#n:g)+_s):#r]";
        //N =NAME
        //G =




        List<String[]> newTemplates1 = new ArrayList<>();
//        newTemplates.add(new String[]{
//                "שאלה",
//                "תשובה",
//                "תשובה שגויה 1",
//                "תשובה שגויה 2",
//                "תשובה שגויה 2",
//                "רמז"
//        });

        newTemplates1.add(new String[]{
                "[HUMAN:#n] [VERB:id=buy:(past_+(#n:g)+_s):#r] [NUM:min=10;max=20:#X] [ITEM:type=STATIONERY:p:#2] כל [#2:s]" +
                       " עולה [NUM:min=5;max=10:#Q]. כמה עלה כל הקניה?" +

                        "[NUM:min=0;max=1:*:#E]"+

                        "[IF:(#E)=0:< >:<  >]",
                "[#X:mul_(#Q)]",
                "[NUM:valve=(#Q:add_5):mul_2]",
                "",
                "",
                "vf"
                }
        );

        newTemplates1.add(new String[]{
                        "[PLACE:place_type=STORE:*:#a] [ITEM:type=(#a:t)]",
                        "",
                        "",
                        "",
                        "",
                        ""
                }
        );

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

        // 9. תבנית קניות משותפות (חברים קונים יחד ומקבלים עודף או מוסיפים פריט משותף)
        newTemplates.add(new String[]{
                "[PLACE:place_type=FOOD_SERVICE|ENTERTAINMENT:*:#P1]" +
                        "[ITEM:type=(#P1:t);unit_type=COUNT:*:#I1]" +
                        "[ITEM:type=(#P1:t);unit_type=COUNT;id=!(#I1:id):*:#I2]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=3;max=6:*:#FRIENDS]" +
                        "[NUM:min=10;max=20:*:#P_PRICE]" +
                        "[NUM:min=15;max=35:*:#SHARED_PRICE]" +
                        "[NUM:value=(#FRIENDS:mul_(#P_PRICE)):*:#TOTAL_P]" +
                        "[NUM:value=(#TOTAL_P:add_(#SHARED_PRICE)):*:#TOTAL_COST]" +
                        "[NUM:value=200:*:#BILL]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=buy:(past_+(#1:g)+_s)] [#FRIENDS] [#I1:p] ב[#P1:s] לחברים. כל [#I1:s] עולה [#P_PRICE] שקלים. " +
                        "[IF:(#W)=0:<בנוסף, [#1:he_she] [VERB:id=buy:(past_+(#1:g)+_s)] גם [#I2:s] עבור כולם ב-[#SHARED_PRICE] שקלים. כמה שקלים סך הכל [#1:he_she] [VERB:id=pay:(past_+(#1:g)+_s)]?>" +
                        ":<בנוסף, [#1:he_she] [VERB:id=buy:(past_+(#1:g)+_s)] [#I2:s] ב-[#SHARED_PRICE] שקלים, ו[VERB:id=pay:(past_+(#1:g)+_s)] לקופאי בשטר של 200 שקלים. כמה עודף מגיע [#1:to_him_her]?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST):#R]>:<[NUM:value=(#BILL:sub_(#TOTAL_COST)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_P):#R]>:<[NUM:value=(#BILL:sub_(#TOTAL_P)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST:sub_10):#R]>:<[NUM:value=(#TOTAL_COST):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_P:add_(#FRIENDS)):#R]>:<[NUM:value=(#BILL:sub_(#SHARED_PRICE)):#R]>]",
                "[IF:(#W)=0:<חשב קודם את המחיר של כל [#I1:p] יחד (כפל), ואז הוסף את המחיר של [#I2:s].>:<חשב את העלות הכוללת (כפל ואז חיבור), וחסר את התוצאה מ-200.>]"
        });

        // 10. תבנית עבודה/סידור לפי קצב שעות (מטרה שבועית/יומית)
        newTemplates.add(new String[]{
                "[PLACE:place_type=EDUCATION|HOME:*:#P1]" +
                        "[ITEM:type=STATIONERY;unit_type=COUNT:*:#I1]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=6;max=12:*:#PER_HOUR]" +
                        "[NUM:min=3;max=6:*:#HOURS]" +
                        "[NUM:value=(#PER_HOUR:mul_(#HOURS)):*:#BASE_DONE]" +
                        "[NUM:min=15;max=30:*:#EXTRA]" +
                        "[NUM:value=(#BASE_DONE:add_(#EXTRA)):*:#TOTAL_GOAL]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=arrange:(present_+(#1:g)+_s)] [#I1:p] ב[#P1:s]. בכל שעה [#1:he_she] [VERB:id=arrange:(present_+(#1:g)+_s)] בדיוק [#PER_HOUR] [#I1:p]. " +
                        "[IF:(#W)=0:<אם [#1:he_she] [VERB:id=work:(past_+(#1:g)+_s)] במשך [#HOURS] שעות, ובערב [VERB:id=arrange:(past_+(#1:g)+_s)] עוד [#EXTRA] [#I1:p], כמה [#I1:p] [#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s)] סך הכל?>" +
                        ":<המטרה הייתה לסדר [#TOTAL_GOAL] [#I1:p]. אם [#1:he_she] [VERB:id=work:(past_+(#1:g)+_s)] רק במשך [#HOURS] שעות, כמה [#I1:p] עוד נשארו [#1:to_him_her] לסדר?>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE_DONE:add_(#EXTRA)):#R]>:<[NUM:value=(#TOTAL_GOAL:sub_(#BASE_DONE)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE_DONE:sub_(#EXTRA)):abs:#R]>:<[NUM:value=(#BASE_DONE:add_(#EXTRA)):#R]>]",
                "[NUM:value=(#BASE_DONE):#R]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_GOAL:sub_10):#R]>:<[NUM:value=(#TOTAL_GOAL:sub_(#HOURS)):#R]>]",
                "[IF:(#W)=0:<הכפל את קצב הסידור במספר השעות, והוסף את הכמות של הערב.>:<הכפל את קצב הסידור במספר השעות, וחסר את התוצאה מתוך המטרה הכוללת.>]"
        });

// 11. תבנית שורות במערך (מטריצה) של פריטים + בלאי
        newTemplates.add(new String[]{
                "[PLACE:place_type=OUTDOORS|PUBLIC:*:#P1]" +
                        "[ITEM:type=(#P1:t):*:#I1]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=6;max=12:*:#ROWS]" +
                        "[NUM:min=8;max=15:*:#PER_ROW]" +
                        "[NUM:value=(#ROWS:mul_(#PER_ROW)):*:#TOTAL_ITEMS]" +
                        "[NUM:min=10;max=25:*:#BROKEN]" +
                        "[NUM:min=2;max=5:*:#EXTRA_ROWS]" +
                        "[NUM:value=(#EXTRA_ROWS:mul_(#PER_ROW)):*:#ADDED_ITEMS]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=arrange:(past_+(#1:g)+_s)] [IF:(#I1:allowed_unit)=NONE:<שורות של [#I1:p]>:<[UNIT:type=(#I1:allowed_unit);item_category=(#I1:t):p:#U1] של [#I1:p]>] ב[#P1:s]. " +
                        "[#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s)] [#ROWS] [IF:(#I1:allowed_unit)=NONE:<שורות>:<[#U1:p]>], ובכל [IF:(#I1:allowed_unit)=NONE:<שורה>:<[#U1:s]>] בדיוק [#PER_ROW] פריטים. " +
                        "[IF:(#W)=0:<לרוע המזל, [#BROKEN] פריטים נהרסו ונזרקו. כמה פריטים תקינים נשארו בסוף?>" +
                        ":<אחרי מנוחה, [#1:he_she] [VERB:id=add:(past_+(#1:g)+_s)] עוד [#EXTRA_ROWS] [IF:(#I1:allowed_unit)=NONE:<שורות מלאות>:<[#U1:p]>]. כמה פריטים יש עכשיו סך הכל?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_ITEMS:sub_(#BROKEN)):#R]>:<[NUM:value=(#TOTAL_ITEMS:add_(#ADDED_ITEMS)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_ITEMS:add_(#BROKEN)):#R]>:<[NUM:value=(#TOTAL_ITEMS:sub_(#ADDED_ITEMS)):#R]>]",
                "[NUM:value=(#TOTAL_ITEMS):#R]",
                "[IF:(#W)=0:<[NUM:value=(#ROWS:mul_(#BROKEN)):#R]>:<[NUM:value=(#TOTAL_ITEMS:add_(#EXTRA_ROWS)):#R]>]",
                "[IF:(#W)=0:<חשב כמה פריטים היו בהתחלה (כמות הקבוצות כפול כמות הפריטים בכל אחת), וחסר את אלו שנהרסו.>:<חשב כמה פריטים היו בהתחלה (כפל), והוסף את מספר הפריטים שנוספו לאחר מכן (כפל נוסף).>]"
        });

        // 12. תבנית מרחק הקפות אימון (מספרים עגולים גדולים יותר - כפל עשרות ומאות)
        newTemplates.add(new String[]{
                "[PLACE:place_type=OUTDOORS|PUBLIC:*:#P1]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=30;max=60;round=10:*:#LAP_LEN]" +
                        "[NUM:min=3;max=6:*:#LAPS]" +
                        "[NUM:value=(#LAP_LEN:mul_(#LAPS)):*:#TOTAL_DIST]" +
                        "[NUM:min=20;max=50;round=10:*:#REMAINING]" +
                        "[NUM:value=(#TOTAL_DIST:add_(#REMAINING)):*:#GOAL]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=run:(present_+(#1:g)+_s)] במסלול מעגלי ב[#P1:s]. אורך כל הקפה הוא [#LAP_LEN] מטרים. " +
                        "[IF:(#W)=0:<אם [#1:he_she] [VERB:id=run:(past_+(#1:g)+_s)] [#LAPS] הקפות, ונשארו [#1:to_him_her] עוד [#REMAINING] מטרים לסיום האימון, מה אורך האימון הכולל במטרים?>" +
                        ":<המטרה של [#1:n] היא לרוץ [#GOAL] מטרים. לאחר השלמת [#LAPS] הקפות מלאות, כמה מטרים עוד נשארו [#1:to_him_her] לרוץ?>]",
                "[IF:(#W)=0:<[NUM:value=(#GOAL):#R]>:<[NUM:value=(#REMAINING):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_DIST:sub_(#REMAINING)):#R]>:<[NUM:value=(#GOAL:add_(#TOTAL_DIST)):#R]>]",
                "[NUM:value=(#TOTAL_DIST):#R]",
                "[IF:(#W)=0:<[NUM:value=(#GOAL:sub_20):#R]>:<[NUM:value=(#REMAINING:add_20):#R]>]",
                "[IF:(#W)=0:<חשב את המרחק ש[#1:he_she] כבר [VERB:id=run:(past_+(#1:g)+_s)] (מספר הקפות כפול אורך הקפה), וחבר למרחק שנשאר.>:<חשב את המרחק ש[#1:he_she] כבר [VERB:id=run:(past_+(#1:g)+_s)] (כפל), וחסר אותו מתוך יעד האימון הכולל.>]"
        });

// 13. תבנית עומס ומשקלים (עגלות/משאיות)
        newTemplates.add(new String[]{
                "[PLACE:place_type=STORE|HOME:*:#P1]" +
                        "[ITEM:type=(#P1:t):*:#I1]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=4;max=8:*:#BOXES]" +
                        "[NUM:min=12;max=25:*:#WEIGHT_PER]" +
                        "[NUM:value=(#BOXES:mul_(#WEIGHT_PER)):*:#TOTAL_WEIGHT]" +
                        "[NUM:min=35;max=60:*:#EXTRA_WEIGHT]" +
                        "[NUM:value=(#TOTAL_WEIGHT:add_(#EXTRA_WEIGHT)):*:#MAX_CAPACITY]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=arrange:(past_+(#1:g)+_s)] [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[UNIT:type=(#I1:allowed_unit);item_category=(#I1:t):p:#U1] של [#I1:p]>] ב[#P1:s]. " +
                        "המשקל של כל [IF:(#I1:allowed_unit)=NONE:<[#I1:s]>:<[#U1:s]>] הוא בדיוק [#WEIGHT_PER] קילוגרמים. [#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s)] [#BOXES] [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[#U1:p]>] על העגלה. " +
                        "[IF:(#W)=0:<בנוסף, [#1:he_she] [VERB:id=add:(past_+(#1:g)+_s)] לעגלה פריט בודד ששוקל [#EXTRA_WEIGHT] ק\"ג. מה המשקל הכולל על העגלה כעת?>" +
                        ":<העגלה יכולה לשאת מקסימום [#MAX_CAPACITY] קילוגרמים. כמה קילוגרמים נוספים אפשר להעמיס עליה לפני שתשבר?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_WEIGHT:add_(#EXTRA_WEIGHT)):#R]>:<[NUM:value=(#MAX_CAPACITY:sub_(#TOTAL_WEIGHT)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_WEIGHT:sub_(#EXTRA_WEIGHT)):#R]>:<[NUM:value=(#MAX_CAPACITY:add_(#TOTAL_WEIGHT)):#R]>]",
                "[NUM:value=(#TOTAL_WEIGHT):#R]",
                "[IF:(#W)=0:<[NUM:value=(#MAX_CAPACITY:sub_10):#R]>:<[NUM:value=(#EXTRA_WEIGHT:add_10):#R]>]",
                "[IF:(#W)=0:<חשב את המשקל של [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[#U1:p]>] יחד (כפל), ואז הוסף את המשקל של הפריט הבודד.>:<חשב את המשקל של [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[#U1:p]>] שכבר על העגלה (כפל), וחסר אותו מהקיבולת המקסימלית.>]"
        });


        // ==========================================
        // easy templates (1-10)
        // ==========================================

        // easy 1 - אסף ואיבד (חיבור / חיסור)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1]" +
                        "[ITEM:unit_type=COUNT:*:#I1]" +
                        "[NUM:min=10;max=25:*:#START]" +
                        "[NUM:min=3;max=8:*:#DELTA]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=collect:(past_+(#1:g)+_s)] [#START] [#I1:p]. " +
                        "[IF:(#W)=0:<אחר כך [#1:he_she] [VERB:id=find:(past_+(#1:g)+_s)] עוד [#DELTA] [#I1:p]. כמה [#I1:p] יש ל[#1:n] סך הכל?>" +
                        ":<אחר כך [#1:he_she] [VERB:id=lose:(past_+(#1:g)+_s)] [#DELTA] [#I1:p]. כמה [#I1:p] נשאר ל[#1:n]?>]",
                "[IF:(#W)=0:<[NUM:value=(#START:add_(#DELTA)):#R]>:<[NUM:value=(#START:sub_(#DELTA)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#START:sub_(#DELTA)):#R]>:<[NUM:value=(#START:add_(#DELTA)):#R]>]",
                "[NUM:value=(#START):#R]",
                "[NUM:value=(#DELTA):#R]",
                "[IF:(#W)=0:<חבר את המספר ההתחלתי עם מה שנמצא.>:<חסר מהמספר ההתחלתי את מה שאבד.>]"
        });

        // easy 2 - ארגזים ופריטים (כפל / חילוק)
        newTemplates.add(new String[]{
                "[PLACE:place_type=STORE:*:#P1]" +
                        "[ITEM:type=(#P1:t);unit_type=COUNT:*:#I1]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=2;max=6:*:#BOXES]" +
                        "[NUM:min=4;max=8:*:#PER_BOX]" +
                        "[NUM:value=(#BOXES:mul_(#PER_BOX)):*:#TOTAL]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<ב[#P1:s] יש [#BOXES] ארגזים. בכל ארגז יש [#PER_BOX] [#I1:p]. כמה [#I1:p] יש בסך הכל [#P1:in_it]?>" +
                        ":<ב[#P1:s] יש [#TOTAL] [#I1:p]. [#1:n] [VERB:id=arrange:(past_+(#1:g)+_s)] אותם בארגזים, [#PER_BOX] [#I1:p] בכל ארגז. כמה ארגזים נדרשו?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#BOXES):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BOXES:add_(#PER_BOX)):#R]>:<[NUM:value=(#PER_BOX):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_(#PER_BOX)):#R]>:<[NUM:value=(#TOTAL:sub_(#BOXES)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BOXES:mul_2):#R]>:<[NUM:value=(#TOTAL:div_(#BOXES)):#R]>]",
                "[IF:(#W)=0:<כפל את מספר הארגזים בכמות שבכל ארגז.>:<חלק את הכמות הכוללת בכמות שבכל ארגז.>]"
        });

        // easy 3 - חלוקה שווה (חלוקה / כפל)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1]" +
                        "[ITEM:unit_type=COUNT:*:#I1]" +
                        "[NUM:min=3;max=6:*:#FACTOR]" +
                        "[NUM:min=2;max=7:*:#QUOTIENT]" +
                        "[NUM:value=(#FACTOR:mul_(#QUOTIENT)):*:#TOTAL]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<ל[#1:n] יש [#TOTAL] [#I1:p]. [#1:he_she] [VERB:id=divide:(past_+(#1:g)+_s)] אותם שווה בשווה בין [#FACTOR] חברים. כמה [#I1:p] קיבל כל חבר?>" +
                        ":<[#1:n] רוצה לתת [#QUOTIENT] [#I1:p] לכל [#FACTOR] חברים. כמה [#I1:p] [#1:he_she] צריך סך הכל?>]",
                "[IF:(#W)=0:<[NUM:value=(#QUOTIENT):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#FACTOR):#R]>:<[NUM:value=(#FACTOR:add_(#QUOTIENT)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_(#FACTOR)):#R]>:<[NUM:value=(#QUOTIENT:mul_2):#R]>]",
                "[NUM:value=(#QUOTIENT:add_(#FACTOR)):#R]",
                "[IF:(#W)=0:<חלק את הכמות הכוללת במספר החברים.>:<כפל את הכמות לכל חבר במספר החברים.>]"
        });

        // easy 4 - חיסכון יומי (כפל / כפל + חיבור)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1]" +
                        "[NUM:min=2;max=4:*:#DAYS]" +
                        "[NUM:min=5;max=10:*:#PER_DAY]" +
                        "[NUM:value=(#DAYS:mul_(#PER_DAY)):*:#BASE]" +
                        "[NUM:min=10;max=20:*:#BONUS]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=save:(past_+(#1:g)+_s)] [#PER_DAY] שקלים בכל יום במשך [#DAYS] ימים. " +
                        "[IF:(#W)=0:<כמה שקלים [#1:n] [VERB:id=save:(past_+(#1:g)+_s)] סך הכל?>" +
                        ":<בנוסף, [#1:he_she] [VERB:id=receive:(past_+(#1:g)+_s)] [#BONUS] שקלים כמתנה. כמה שקלים יש ל[#1:n] עכשיו?>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE):#R]>:<[NUM:value=(#BASE:add_(#BONUS)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#DAYS:add_(#PER_DAY)):#R]>:<[NUM:value=(#BONUS:add_(#PER_DAY)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PER_DAY):#R]>:<[NUM:value=(#BASE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE:add_1):#R]>:<[NUM:value=(#BONUS:mul_(#DAYS)):#R]>]",
                "[IF:(#W)=0:<כפל את הסכום היומי במספר הימים.>:<כפל את הסכום היומי במספר הימים, ואז הוסף את המתנה.>]"
        });

        // easy 5 - כיתה (כפל / חלוקה)
        newTemplates.add(new String[]{
                "[PLACE:place_type=EDUCATION:*:#P1]" +
                        "[ITEM:type=STATIONERY;unit_type=COUNT:*:#I1]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=4;max=8:*:#STUDENTS]" +
                        "[NUM:min=2;max=5:*:#EACH]" +
                        "[NUM:value=(#STUDENTS:mul_(#EACH)):*:#TOTAL]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "ב[#P1:s] יש [#STUDENTS] תלמידים. " +
                        "[IF:(#W)=0:<המורה [#1:n] [VERB:id=give:(past_+(#1:g)+_s)] לכל תלמיד [#EACH] [#I1:p]. כמה [#I1:p] חולקו בסך הכל?>" +
                        ":<ל[#1:n] יש [#TOTAL] [#I1:p] לחלק שווה בשווה. כמה [#I1:p] יקבל כל תלמיד?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#EACH):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#STUDENTS:add_(#EACH)):#R]>:<[NUM:value=(#STUDENTS):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#EACH:mul_2):#R]>:<[NUM:value=(#TOTAL:sub_(#STUDENTS)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_(#STUDENTS)):#R]>:<[NUM:value=(#STUDENTS:mul_2):#R]>]",
                "[IF:(#W)=0:<כפל את מספר התלמידים בכמות שכל תלמיד קיבל.>:<חלק את הכמות הכוללת במספר התלמידים.>]"
        });

        // easy 6 - שני חברים יחד (חיבור / חיסור)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1]" +
                        "[HUMAN:n=!(#1:n):*:#3]" +
                        "[ITEM:unit_type=COUNT:*:#I1]" +
                        "[NUM:min=8;max=20:*:#A]" +
                        "[NUM:min=5;max=15:*:#B]" +
                        "[NUM:value=(#A:add_(#B)):*:#TOTAL]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<ל[#1:n] יש [#A] [#I1:p] ול[#3:n] יש [#B] [#I1:p]. כמה [#I1:p] יש לשניהם ביחד?>" +
                        ":<יחד ל[#1:n] ול[#3:n] יש [#TOTAL] [#I1:p]. ל[#1:n] יש [#A] [#I1:p]. כמה [#I1:p] יש ל[#3:n]?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#B):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#A:sub_(#B)):#R]>:<[NUM:value=(#A):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#A):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#B:mul_2):#R]>:<[NUM:value=(#TOTAL:add_(#A)):#R]>]",
                "[IF:(#W)=0:<פשוט חבר את שתי הכמויות יחד.>:<חסר את הכמות של [#1:n] מהסכום הכולל.>]"
        });

        // easy 7 - שורות ועמודות (כפל / חלוקה)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1]" +
                        "[ITEM:unit_type=COUNT:*:#I1]" +
                        "[NUM:min=3;max=6:*:#ROWS]" +
                        "[NUM:min=4;max=8:*:#PER_ROW]" +
                        "[NUM:value=(#ROWS:mul_(#PER_ROW)):*:#TOTAL]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=arrange:(past_+(#1:g)+_s)] [#I1:p] בשורות. " +
                        "[IF:(#W)=0:<[#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s)] [#ROWS] שורות, ובכל שורה בדיוק [#PER_ROW] [#I1:p]. כמה [#I1:p] יש בסך הכל?>" +
                        ":<[#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s)] [#TOTAL] [#I1:p] בשורות של [#PER_ROW] [#I1:p] כל אחת. כמה שורות יש?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#ROWS):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#ROWS:add_(#PER_ROW)):#R]>:<[NUM:value=(#PER_ROW):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_(#PER_ROW)):#R]>:<[NUM:value=(#TOTAL:sub_(#ROWS)):#R]>]",
                "[NUM:value=(#ROWS:mul_2):#R]",
                "[IF:(#W)=0:<כפל את מספר השורות בכמות שבכל שורה.>:<חלק את הכמות הכוללת בכמות שבכל שורה.>]"
        });

        // easy 8 - קנייה בתקציב (כפל / חלוקה)
        newTemplates.add(new String[]{
                "[PLACE:place_type=STORE:*:#P1]" +
                        "[ITEM:type=(#P1:t);unit_type=COUNT:*:#I1]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=2;max=8:*:#N]" +
                        "[NUM:min=3;max=7:*:#PRICE]" +
                        "[NUM:value=(#N:mul_(#PRICE)):*:#BUDGET]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[IF:(#W)=0:<[#1:n] [VERB:id=buy:(past_+(#1:g)+_s)] ב[#P1:s] [#N] [#I1:p]. כל [#I1:s] עולה [#PRICE] שקלים. כמה שקלים [#1:n] [VERB:id=pay:(past_+(#1:g)+_s)] סך הכל?>" +
                        ":<ל[#1:n] יש בדיוק [#BUDGET] שקלים. [#I1:p] עולים [#PRICE] שקלים כל [#I1:one]. כמה [#I1:p] [#1:he_she] יכול לקנות?>]",
                "[IF:(#W)=0:<[NUM:value=(#BUDGET):#R]>:<[NUM:value=(#N):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#N:add_(#PRICE)):#R]>:<[NUM:value=(#PRICE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BUDGET:sub_(#PRICE)):#R]>:<[NUM:value=(#BUDGET:div_(#N)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PRICE:mul_2):#R]>:<[NUM:value=(#BUDGET:sub_(#PRICE)):#R]>]",
                "[IF:(#W)=0:<כפל את מספר הפריטים במחיר כל פריט.>:<חלק את התקציב במחיר של פריט אחד.>]"
        });

        // easy 9 - הכנסה ורכישה (כפל / כפל וחיסור)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1]" +
                        "[NUM:min=3;max=5:*:#DAYS]" +
                        "[NUM:min=6;max=10:*:#EARNED]" +
                        "[NUM:value=(#DAYS:mul_(#EARNED)):*:#BASE]" +
                        "[NUM:min=5;max=15:*:#SPENT]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=work:(past_+(#1:g)+_s)] ו[VERB:id=receive:(past_+(#1:g)+_s)] [#EARNED] שקלים בכל יום. [#1:he_she] [VERB:id=work:(past_+(#1:g)+_s)] [#DAYS] ימים. " +
                        "[IF:(#W)=0:<כמה שקלים [#1:n] הרוויח סך הכל?>" +
                        ":<לאחר מכן [#1:he_she] [VERB:id=pay:(past_+(#1:g)+_s)] [#SPENT] שקלים. כמה שקלים נשארו ל[#1:n]?>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE):#R]>:<[NUM:value=(#BASE:sub_(#SPENT)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#DAYS:add_(#EARNED)):#R]>:<[NUM:value=(#SPENT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#EARNED):#R]>:<[NUM:value=(#BASE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE:add_5):#R]>:<[NUM:value=(#BASE:sub_(#DAYS)):#R]>]",
                "[IF:(#W)=0:<כפל את הסכום היומי במספר הימים.>:<כפל את הסכום היומי במספר הימים, ואז חסר את מה ששילם.>]"
        });

        // easy 10 - קיבל ונתן (חיבור / חיבור וחיסור)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1]" +
                        "[HUMAN:n=!(#1:n):*:#3]" +
                        "[ITEM:unit_type=COUNT:*:#I1]" +
                        "[NUM:min=15;max=25:*:#START]" +
                        "[NUM:min=4;max=9:*:#GOT]" +
                        "[NUM:min=2;max=7:*:#GAVE]" +
                        "[NUM:value=(#START:add_(#GOT)):*:#MID]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "ל[#1:n] [VERB:id=be:(past_+(#1:g)+_s)] [#START] [#I1:p]. " +
                        "[#3:n] [VERB:id=give:(past_+(#3:g)+_s)] ל[#1:n] עוד [#GOT] [#I1:p]. " +
                        "[IF:(#W)=0:<כמה [#I1:p] יש ל[#1:n] עכשיו?>" +
                        ":<לאחר מכן, [#1:n] [VERB:id=give:(past_+(#1:g)+_s)] ל[#3:n] בחזרה [#GAVE] [#I1:p]. כמה [#I1:p] נשאר ל[#1:n]?>]",
                "[IF:(#W)=0:<[NUM:value=(#MID):#R]>:<[NUM:value=(#MID:sub_(#GAVE)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#START:sub_(#GOT)):#R]>:<[NUM:value=(#START:sub_(#GAVE)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#GOT):#R]>:<[NUM:value=(#MID):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#START:add_1):#R]>:<[NUM:value=(#START:add_(#GAVE)):#R]>]",
                "[IF:(#W)=0:<חבר את מה שהתחיל עם מה שקיבל.>:<חבר את מה שהתחיל עם מה שקיבל, ואז חסר את מה שנתן.>]"
        });
        // תבנית 14: תנועה ומהירות (דרך = מהירות * זמן)
        newTemplates.add(new String[]{
                // שאלה
                "[HUMAN:*:#1][PLACE:place_type=OUTDOORS:*:#P1][NUM:min=15;max=40:*:#SPEED][NUM:min=3;max=8:*:#TIME][NUM:value=(#SPEED:mul_(#TIME)):*:#DIST][NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=run:(past_+(#1:g)+_s)] ב[#P1:s]. הקצב היה בדיוק [#SPEED] מטרים בדקה. [IF:(#W)=0:<אם [#1:he_she] [VERB:id=run:(past_+(#1:g)+_s)] במשך [#TIME] דקות ברצף, איזה מרחק [#1:he_she] עבר/ה בסך הכל?>:<אם [#1:he_she] עבר/ה מרחק כולל של [#DIST] מטרים באותו הקצב, כמה דקות נמשכה הריצה?>]",
                // תשובה נכונה
                "[IF:(#W)=0:<[NUM:value=(#DIST):#R]>:<[NUM:value=(#TIME):#R]>]",
                // תשובות שגויות
                "[IF:(#W)=0:<[NUM:value=(#DIST:add_(#SPEED)):#R]>:<[NUM:value=(#TIME:add_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SPEED:add_(#TIME)):#R]>:<[NUM:value=(#DIST:sub_(#SPEED)):#R]>]",
                "[NUM:value=(#SPEED:mul_10):#R]",
                // רמז
                "[IF:(#W)=0:<כדי למצוא את הדרך, כפול את המהירות (קצב) בזמן.>:<כדי למצוא את הזמן, חלק את הדרך (המרחק הכולל) במהירות.>]"
        });

        // תבנית 15: חיסכון ומשוואה קווית (סכום התחלתי + קצב קבוע)
        newTemplates.add(new String[]{
                // שאלה
                "[HUMAN:*:#1][ITEM:type=MONEY|ENTERTAINMENT:*:#I1][NUM:min=50;max=120:*:#START][NUM:min=10;max=25:*:#RATE][NUM:min=4;max=8:*:#DAYS][NUM:value=(#RATE:mul_(#DAYS)):*:#ADDED][NUM:value=(#START:add_(#ADDED)):*:#TOTAL][NUM:min=0;max=1:*:#W]" +
                        "[#1:n] אוסף/ת [#I1:p]. בהתחלה היו [#1:to_him_her] [#START] [#I1:p], ובכל יום [#1:he_she] מוסיף/פה לאוסף עוד [#RATE] [#I1:p]. [IF:(#W)=0:<כמה [#I1:p] יהיו ל-[#1:n] בעוד [#DAYS] ימים?>:<לאחר מספר ימים היו באוסף [#TOTAL] [#I1:p]. כמה ימים עברו מאז שהתחיל/ה לאסוף?>]",
                // תשובה נכונה
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#DAYS):#R]>]",
                // תשובות שגויות
                "[IF:(#W)=0:<[NUM:value=(#ADDED):#R]>:<[NUM:value=(#DAYS:add_1):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_(#RATE)):#R]>:<[NUM:value=(#TOTAL:sub_(#START)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#START:add_(#DAYS)):#R]>:<[NUM:value=(#TOTAL:div_(#RATE)):#R]>]",
                // רמז
                "[IF:(#W)=0:<חשב קודם כמה פריטים נוספו (קצב כפול ימים), ואז חבר לסכום ההתחלתי.>:<חסר את הכמות ההתחלתית מהכמות הסופית, ואת התוצאה חלק בכמות שמתווספת כל יום.>]"
        });

        // תבנית 16: ממוצעים פשוטים של 2 מספרים
        newTemplates.add(new String[]{
                // שאלה
                "[HUMAN:*:#1][ITEM:*:#I1][PLACE:*:#P1][NUM:min=20;max=40:*:#AVG][NUM:min=4;max=12:*:#DEV][NUM:value=(#AVG:sub_(#DEV)):*:#D1][NUM:value=(#AVG:add_(#DEV)):*:#D2][NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=collect:(past_+(#1:g)+_s)] [#I1:p] ב[#P1:s]. ביום הראשון [#1:he_she] [VERB:id=collect:(past_+(#1:g)+_s)] [#D1] [#I1:p], וביום השני [#D2] [#I1:p]. [IF:(#W)=0:<מהו הממוצע של מספר ה-[#I1:p] שנאספו ביום?>:<אם הממוצע היומי היה [#AVG], וכמות ה-[#I1:p] ביום השני הייתה [#D2], כמה [#I1:p] נאספו ביום הראשון?>]",
                // תשובה נכונה
                "[IF:(#W)=0:<[NUM:value=(#AVG):#R]>:<[NUM:value=(#D1):#R]>]",
                // תשובות שגויות
                "[IF:(#W)=0:<[NUM:value=(#D1:add_(#D2)):#R]>:<[NUM:value=(#D2:sub_(#AVG)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AVG:add_2):#R]>:<[NUM:value=(#AVG:sub_(#DEV)):add_(#DEV):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#D2:sub_(#D1)):#R]>:<[NUM:value=(#D1:add_2):#R]>]",
                // רמז
                "[IF:(#W)=0:<כדי למצוא ממוצע של שני ימים, חבר את שתי הכמויות וחלק ב-2.>:<כפול את הממוצע ב-2 כדי למצוא את הסכום הכולל, ואז חסר ממנו את הכמות של היום השני.>]"
        });

        // תבנית 17: יחסים וכפולות (פי X יותר)
        newTemplates.add(new String[]{
                // שאלה
                "[HUMAN:*:#1][HUMAN:n=!(#1:n):*:#2][ITEM:*:#I1][PLACE:*:#P1][NUM:min=10;max=20:*:#H1][NUM:min=3;max=6:*:#RATIO][NUM:value=(#H1:mul_(#RATIO)):*:#H2][NUM:value=(#H1:add_(#H2)):*:#TOTAL][NUM:min=0;max=1:*:#W]" +
                        "[#1:n] ו[#2:n] מסדרים [#I1:p] ב[#P1:s]. ל-[#1:n] יש [#H1] [#I1:p]. ל-[#2:n] יש פי [#RATIO] יותר מאשר ל-[#1:n]. [IF:(#W)=0:<כמה [#I1:p] יש לשניהם יחד?>:<בכמה [#I1:p] יותר יש ל-[#2:n] מאשר ל-[#1:n]?>]",
                // תשובה נכונה
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#H2:sub_(#H1)):#R]>]",
                // תשובות שגויות
                "[IF:(#W)=0:<[NUM:value=(#H2):#R]>:<[NUM:value=(#H2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#H1:add_(#RATIO)):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:add_(#H1)):#R]>:<[NUM:value=(#H1:mul_2):#R]>]",
                // רמז
                "[IF:(#W)=0:<חשב קודם כמה יש ל-[#2:n] (כפל), ואז חבר לכמות של [#1:n].>:<חשב כמה יש ל-[#2:n] (כפל), ואז חסר מזה את הכמות של [#1:n] כדי למצוא את ההפרש.>]"
        });

        // תבנית 18: שטח (מערכים ורשתות - כפל שורות בעמודות)
        newTemplates.add(new String[]{
                // שאלה
                "[PLACE:place_type=HOME|EDUCATION:*:#P1][ITEM:type=STATIONERY|FOOD:*:#I1][HUMAN:*:#1][NUM:min=6;max=12:*:#WIDTH][NUM:min=15;max=25:*:#LENGTH][NUM:value=(#WIDTH:mul_(#LENGTH)):*:#AREA][NUM:min=0;max=1:*:#W]" +
                        "[#1:n] מסדר/ת [#I1:p] בצורת מלבן גדול ב[#P1:s]. בשורה אחת יש [#LENGTH] [#I1:p], ובעמודה אחת יש [#WIDTH] [#I1:p]. [IF:(#W)=0:<כמה [#I1:p] סך הכל יש במלבן המלא?>:<אם המלבן המלא מכיל בדיוק [#AREA] [#I1:p], ובשורה אחת יש [#LENGTH] [#I1:p], כמה [#I1:p] יש בעמודה אחת?>]",
                // תשובה נכונה
                "[IF:(#W)=0:<[NUM:value=(#AREA):#R]>:<[NUM:value=(#WIDTH):#R]>]",
                // תשובות שגויות
                "[IF:(#W)=0:<[NUM:value=(#WIDTH:add_(#LENGTH)):#R]>:<[NUM:value=(#AREA:div_10):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#WIDTH:add_(#LENGTH)):mul_2:#R]>:<[NUM:value=(#LENGTH:sub_(#WIDTH)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AREA:sub_(#WIDTH)):#R]>:<[NUM:value=(#WIDTH:add_2):#R]>]",
                // רמז
                "[IF:(#W)=0:<כדי למצוא את הכמות הכוללת במלבן (שטח), כפול את מספר הפריטים בשורה במספר הפריטים בעמודה.>:<חלק את הכמות הכוללת במספר הפריטים שבשורה אחת.>]"
        });

        // תבנית 19: שברים פשוטים (חלוקה לפי מכנה)
        newTemplates.add(new String[]{
                // שאלה
                "[HUMAN:*:#1][ITEM:type=FOOD|TOY:*:#I1][PLACE:*:#P1][NUM:min=3;max=6:*:#PARTS][NUM:min=6;max=12:*:#PER_PART][NUM:value=(#PARTS:mul_(#PER_PART)):*:#TOTAL][NUM:value=(#TOTAL:sub_(#PER_PART)):*:#LEFT][NUM:min=0;max=1:*:#W]" +
                        "ל-[#1:n] היו [#TOTAL] [#I1:p] ב[#P1:s]. [#1:he_she] נתן/נה לחבר בדיוק 1 חלקי [#PARTS] מכל ה-[#I1:p] שהיו לו/לה. [IF:(#W)=0:<כמה [#I1:p] קיבל החבר?>:<כמה [#I1:p] נשארו ל-[#1:n] בסוף?>]",
                // תשובה נכונה
                "[IF:(#W)=0:<[NUM:value=(#PER_PART):#R]>:<[NUM:value=(#LEFT):#R]>]",
                // תשובות שגויות
                "[IF:(#W)=0:<[NUM:value=(#LEFT):#R]>:<[NUM:value=(#PER_PART):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_(#PARTS)):#R]>:<[NUM:value=(#TOTAL:sub_(#PARTS)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PARTS):#R]>:<[NUM:value=(#TOTAL:add_(#PARTS)):#R]>]",
                // רמז
                "[IF:(#W)=0:<כדי למצוא כמה זה 1 חלקי [#PARTS], עליך לחלק את הכמות הכוללת ב-[#PARTS].>:<קודם חלק את הכמות הכוללת ב-[#PARTS] כדי לדעת כמה החבר קיבל, ואז חסר את זה מהכמות ההתחלתית.>]"
        });

        // תבנית 20: פרופורציות והכפלת כמויות
        newTemplates.add(new String[]{
                // שאלה
                "[PLACE:place_type=STORE:*:#P1][ITEM:type=(#P1:t):*:#I1][NUM:min=5;max=12:*:#PER_BOX][NUM:min=2;max=4:*:#BASE_BOX][NUM:value=(#BASE_BOX:mul_(#PER_BOX)):*:#BASE_ITEM][NUM:min=6;max=10:*:#NEW_BOX][NUM:value=(#NEW_BOX:mul_(#PER_BOX)):*:#NEW_ITEM][NUM:min=0;max=1:*:#W]" +
                        "ב[#P1:s], ארזו [#BASE_ITEM] [#I1:p] בתוך [#BASE_BOX] קופסאות שוות בגודלן. [IF:(#W)=0:<כמה [#I1:p] יהיו בתוך [#NEW_BOX] קופסאות מאותו סוג?>:<אם יש למפעל [#NEW_ITEM] [#I1:p], לכמה קופסאות מאותו סוג הם יזדקקו?>]",
                // תשובה נכונה
                "[IF:(#W)=0:<[NUM:value=(#NEW_ITEM):#R]>:<[NUM:value=(#NEW_BOX):#R]>]",
                // תשובות שגויות
                "[IF:(#W)=0:<[NUM:value=(#BASE_ITEM:add_(#NEW_BOX)):#R]>:<[NUM:value=(#NEW_ITEM:div_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#NEW_BOX:mul_(#BASE_ITEM)):#R]>:<[NUM:value=(#NEW_BOX:add_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#NEW_ITEM:sub_(#PER_BOX)):#R]>:<[NUM:value=(#NEW_ITEM:sub_(#BASE_ITEM)):#R]>]",
                // רמז
                "[IF:(#W)=0:<חשב תחילה כמה יש בקופסה אחת (על ידי חילוק), ואז כפול במספר הקופסאות החדש.>:<חשב תחילה כמה יש בקופסה אחת, ואז חלק את הכמות הכוללת החדשה בכמות שבקופסה אחת.>]"
        });

        // תבנית 21: שאלת גיל (משוואת חיבור בזמן)
        newTemplates.add(new String[]{
                // שאלה
                "[HUMAN:*:#1][HUMAN:n=!(#1:n):*:#2][NUM:min=10;max=16:*:#AGE1][NUM:min=3;max=7:*:#DIFF][NUM:value=(#AGE1:add_(#DIFF)):*:#AGE2][NUM:min=4;max=10:*:#YEARS][NUM:value=(#AGE1:add_(#YEARS)):*:#F1][NUM:value=(#AGE2:add_(#YEARS)):*:#F2][NUM:value=(#F1:add_(#F2)):*:#SUM_FUTURE][NUM:min=0;max=1:*:#W]" +
                        "[#1:n] בן/בת [#AGE1] כיום. [#2:n] מבוגר/ת מ-[#1:n] ב-[#DIFF] שנים. [IF:(#W)=0:<בעוד [#YEARS] שנים, בן/בת כמה יהיה/תהיה [#2:n]?>:<בעוד [#YEARS] שנים, מה יהיה סכום הגילים של שניהם יחד?>]",
                // תשובה נכונה
                "[IF:(#W)=0:<[NUM:value=(#F2):#R]>:<[NUM:value=(#SUM_FUTURE):#R]>]",
                // תשובות שגויות
                "[IF:(#W)=0:<[NUM:value=(#AGE2):#R]>:<[NUM:value=(#AGE1:add_(#AGE2)):add_(#YEARS):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AGE1:add_(#YEARS)):#R]>:<[NUM:value=(#F1:add_(#AGE2)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#F2:add_(#DIFF)):#R]>:<[NUM:value=(#SUM_FUTURE:sub_(#DIFF)):#R]>]",
                // רמז
                "[IF:(#W)=0:<חשב קודם את הגיל של [#2:n] כיום, ואז הוסף לו [#YEARS] שנים.>:<חשב את הגיל של כל אחד מהם בעוד [#YEARS] שנים (אל תשכח להוסיף [#YEARS] לכל אחד בנפרד!), ואז חבר אותם.>]"
        });

        // תבנית 22: חיסור מדורג מיעד (ניהול מלאי)
        newTemplates.add(new String[]{
                // שאלה
                "[HUMAN:*:#1][ITEM:type=COLLECTIBLE|MONEY:*:#I1][PLACE:*:#P1][NUM:min=300;max=600;round=50:*:#TARGET][NUM:min=80;max=150;round=10:*:#D1][NUM:min=60;max=120;round=10:*:#D2][NUM:value=(#D1:add_(#D2)):*:#COLLECTED][NUM:value=(#TARGET:sub_(#COLLECTED)):*:#LEFT][NUM:min=0;max=1:*:#W]" +
                        "היעד של [#1:n] הוא לאסוף [#TARGET] [#I1:p] ב[#P1:s]. בשבוע הראשון [#1:he_she] [VERB:id=collect:(past_+(#1:g)+_s)] [#D1] [#I1:p], ובשבוע השני עוד [#D2] [#I1:p]. [IF:(#W)=0:<כמה [#I1:p] עוד נשארו ל-[#1:n] לאסוף כדי להגיע ליעד?>:<כמה [#I1:p] [#1:he_she] [VERB:id=collect:(past_+(#1:g)+_s)] בשבועיים הראשונים יחד?>]",
                // תשובה נכונה
                "[IF:(#W)=0:<[NUM:value=(#LEFT):#R]>:<[NUM:value=(#COLLECTED):#R]>]",
                // תשובות שגויות
                "[IF:(#W)=0:<[NUM:value=(#COLLECTED):#R]>:<[NUM:value=(#LEFT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TARGET:sub_(#D1)):#R]>:<[NUM:value=(#COLLECTED:add_(#D2)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#LEFT:add_10):#R]>:<[NUM:value=(#COLLECTED:sub_10):#R]>]",
                // רמז
                "[IF:(#W)=0:<חבר את מה שנאסף בשבוע הראשון והשני, וחסר את הסכום מתוך היעד הכולל.>:<פשוט חבר את שתי הכמויות שנאספו בשבוע הראשון והשני.>]"
        });

        // תבנית 23: היקף מלבן (2 * (אורך + רוחב))
        newTemplates.add(new String[]{
                // שאלה
                "[HUMAN:*:#1][ITEM:type=TOY|STATIONERY:*:#I1][PLACE:place_type=HOME:*:#P1][NUM:min=5;max=12:*:#WIDTH][NUM:min=15;max=25:*:#LENGTH][NUM:value=(#WIDTH:add_(#LENGTH)):*:#HALF][NUM:value=(#HALF:mul_2):*:#PERIMETER][NUM:min=0;max=1:*:#W]" +
                        "[#1:n] מכין/נה מסגרת בצורת מלבן ב[#P1:s] בעזרת [#I1:p]. אורך המסגרת הוא [#LENGTH] [#I1:p] ורוחבה הוא [#WIDTH] [#I1:p]. [IF:(#W)=0:<כמה [#I1:p] יצטרך/תצטרך [#1:n] סך הכל כדי להקיף את כל המסגרת (היקף המלבן)?>:<אם היקף המסגרת דורש בסך הכל [#PERIMETER] [#I1:p], והאורך שלה הוא [#LENGTH] [#I1:p], מהו הרוחב?>]",
                // תשובה נכונה
                "[IF:(#W)=0:<[NUM:value=(#PERIMETER):#R]>:<[NUM:value=(#WIDTH):#R]>]",
                // תשובות שגויות
                "[IF:(#W)=0:<[NUM:value=(#HALF):#R]>:<[NUM:value=(#WIDTH:mul_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#WIDTH:mul_(#LENGTH)):#R]>:<[NUM:value=(#PERIMETER:sub_(#LENGTH)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PERIMETER:sub_(#WIDTH)):#R]>:<[NUM:value=(#PERIMETER:div_2):#R]>]",
                // רמז
                "[IF:(#W)=0:<היקף מלבן שווה לפעמיים האורך ועוד פעמיים הרוחב.>:<כדי למצוא את הרוחב, חלק את ההיקף ב-2 (כדי למצוא סכום של אורך ורוחב אחד), ואז חסר מזה את האורך.>]"
        });
        // 1
        newTemplates.add(new String[]{
                "[PLACE:place_type=STORE:*:#P1][ROLE:place_id=(#P1:id);role_type=OPERATOR:*:#O1][ITEM:type=(#P1:t):*:#I1][ITEM:type=(#P1:t);id=!(#I1:id):*:#I2][HUMAN:*:#1][NUM:min=3;max=6:*:#QTY][NUM:min=5;max=12:*:#PRICE][NUM:value=100:*:#BILL][NUM:min=10;max=20:*:#EXTRA_PRICE][NUM:value=(#QTY:mul_(#PRICE)):*:#TOTAL_COST][NUM:min=0;max=1:*:#W][NUM:min=0;max=1:*:#OG][#1:n] [VERB:id=buy:(past_+(#1:g)+_s)] ב[#P1:s] [IF:(#I1:allowed_unit)=NONE:<[#QTY] [#I1:p]. כל [#I1:s]>:<[UNIT:type=(#I1:allowed_unit);item_category=(#I1:t):*:#U1][#QTY] [#U1:p] של [#I1:p]. כל [#U1:s]>] עולה [#PRICE] שקלים. [IF:(#W)=0:<אם [#1:he_she] [VERB:id=pay:(past_+(#1:g)+_s)] [IF:(#OG)=0:<ל[#O1:sm]>:<ל[#O1:sf]>] בשטר של [#BILL] שקלים, כמה עודף [#1:he_she] [VERB:id=receive:(past_+(#1:g)+_s)]?>:<בנוסף, [#1:he_she] [VERB:id=buy:(past_+(#1:g)+_s)] מה[IF:(#OG)=0:<[#O1:sm]>:<[#O1:sf]>] גם [#I2:s] ב-[#EXTRA_PRICE] שקלים. כמה שקלים סך הכל [#1:he_she] [VERB:id=pay:(past_+(#1:g)+_s)]?>]",
                "[IF:(#W)=0:<[NUM:value=(#BILL:sub_(#TOTAL_COST)):#R]>:<[NUM:value=(#TOTAL_COST:add_(#EXTRA_PRICE)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BILL:sub_(#PRICE)):#R]>:<[NUM:value=(#PRICE:add_(#EXTRA_PRICE)):#R]>]",
                "[NUM:value=(#TOTAL_COST):#R]",
                "[IF:(#W)=0:<[NUM:value=(#BILL:sub_(#QTY)):#R]>:<[NUM:value=(#QTY:add_(#EXTRA_PRICE)):#R]>]",
                "[IF:(#W)=0:<כדי למצוא את העודף, חשב קודם כמה עולים כל ה-[IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[#U1:p]>] יחד (כמות כפול מחיר), וחסר את התוצאה מ-100.>:<חשב קודם את המחיר של כל ה-[IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[#U1:p]>] יחד (כמות כפול מחיר), ואז חבר לסכום את מחיר ה-[#I2:s].>]"
        });

        // 2
        newTemplates.add(new String[]{
                "[PLACE:place_type=HOME|EDUCATION:*:#P1][ITEM:type=(#P1:t);unit_type=COUNT:*:#I1][UNIT:type=(#I1:allowed_unit);item_category=(#I1:t):*:#U1][HUMAN:*:#1][HUMAN:n=!(#1:n):*:#2][NUM:min=3;max=5:*:#BOXES][NUM:min=4;max=9:*:#IN_BOX][NUM:value=(#BOXES:mul_(#IN_BOX)):*:#TARGET][NUM:min=2;max=8:*:#DIFF][NUM:value=(#TARGET:add_(#DIFF)):*:#START_SUB][NUM:value=(#TARGET:sub_(#DIFF)):*:#START_ADD][NUM:min=0;max=1:*:#W][IF:(#W)=0:<ל[#1:n] היו [#START_SUB] [#I1:p]. [#1:he_she] [VERB:id=give:(past_+(#1:g)+_s)] ל[#2:n] [#DIFF] [#I1:p], ואת השאר [VERB:id=divide:(past_+(#1:g)+_s)] שווה בשווה ל-[#BOXES] [#U1:p]. כמה [#I1:p] יש בכל [#U1:s]?>:<ל[#1:n] היו [#START_ADD] [#I1:p]. [#1:he_she] [VERB:id=find:(past_+(#1:g)+_s)] עוד [#DIFF] [#I1:p], ואז [VERB:id=divide:(past_+(#1:g)+_s)] הכל שווה בשווה ל-[#BOXES] [#U1:p]. כמה [#I1:p] יש בכל [#U1:s]?>]",
                "[NUM:value=(#IN_BOX):#R]",
                "[NUM:value=(#TARGET):#R]",
                "[NUM:value=(#TARGET:mul_(#BOXES)):#R]",
                "[NUM:value=(#TARGET:sub_(#BOXES)):#R]",
                "[IF:(#W)=0:<כדי לפתור, קודם חסר את ה-[#I1:p] ש[#1:n] [VERB:id=give:(past_+(#1:g)+_s)], ואז חלק את התוצאה ב-[#BOXES].>:<כדי לפתור, קודם חבר את ה-[#I1:p] ש[#1:n] [VERB:id=find:(past_+(#1:g)+_s)], ואז חלק את התוצאה ב-[#BOXES].>]"
        });

        // 3
        newTemplates.add(new String[]{
                "[PLACE:place_type=FOOD_SERVICE:*:#P1][ROLE:place_id=(#P1:id);role_type=OPERATOR:*:#O1][ITEM:type=(#P1:t);unit_type=COUNT:*:#I1][HUMAN:*:*:#1][NUM:min=8;max=12:*:#MINS_PER][NUM:min=3;max=6:*:#QTY][NUM:value=(#QTY:mul_(#MINS_PER)):*:#TOTAL_MINS][TIME:min=08.00;max=16.00;round=true:*:#T_START][NUM:min=0;max=1:*:#W][#1:n] ה[#O1:s(#1:g)] [VERB:id=prepare:(past_+(#1:g)+_s):#V1] [#I1:p] ב[#P1:s]. הכנת כל [#I1:s] לוקחת בדיוק [#MINS_PER] דקות. [IF:(#W)=0:<אם [#1:he_she] [VERB:id=start:(past_+(#1:g)+_s):#V2] להכין [#QTY] [#I1:p] בשעה [#T_START], באיזו שעה [#1:he_she] [VERB:id=finish:(past_+(#1:g)+_s):#V3]?>:<אם [#1:he_she] [VERB:id=work:(past_+(#1:g)+_s):#V4] ברצף במשך [#TOTAL_MINS] דקות, כמה [#I1:p] [#1:he_she] [VERB:id=prepare:(past_+(#1:g)+_s):#V5] סך הכל?>]",
                "[IF:(#W)=0:<[TIME:value=(#T_START:add_m_(#TOTAL_MINS)):#R]>:<[NUM:value=(#TOTAL_MINS:div_(#MINS_PER)):#R]>]",
                "[IF:(#W)=0:<[TIME:value=(#T_START:add_m_(#MINS_PER)):#R]>:<[NUM:value=(#TOTAL_MINS:mul_(#MINS_PER)):#R]>]",
                "[IF:(#W)=0:<[TIME:value=(#T_START:add_m_(#QTY)):#R]>:<[NUM:value=(#TOTAL_MINS:sub_(#MINS_PER)):#R]>]",
                "[IF:(#W)=0:<[TIME:value=(#T_START:add_m_(#TOTAL_MINS)):add_m_10:#R]>:<[NUM:value=(#QTY:add_1):#R]>]",
                "[IF:(#W)=0:<כדי לפתור, חשב כמה דקות לוקח להכין את כל ה-[#I1:p] יחד (כפל), ואז הוסף את התוצאה לשעת ההתחלה.>:<כדי למצוא את הכמות, חלק את סך כל דקות העבודה ([#TOTAL_MINS]) בזמן שלוקח להכין [#I1:s] אחד ([#MINS_PER]).>]"
        });

        // 4
        newTemplates.add(new String[]{
                "[PLACE:place_type=HOME|EDUCATION:*:#P1][HUMAN:*:#1][NUM:min=10;max=20:*:#DAILY][NUM:min=3;max=6:*:#DAYS][NUM:value=(#DAILY:mul_(#DAYS)):*:#READ][NUM:min=20;max=50:*:#LEFT][NUM:value=(#READ:add_(#LEFT)):*:#TOTAL_PAGES][NUM:min=0;max=1:*:#W][#1:n] [VERB:id=read:(present_+(#1:g)+_s):#V2] ספר ב[#P1:s]. בכל יום [#1:he_she] [VERB:id=read:(present_+(#1:g)+_s):#V2] בדיוק [#DAILY] עמודים. [IF:(#W)=0:<אם בספר יש [#TOTAL_PAGES] עמודים סך הכל, כמה עמודים יישארו ל[#1:n] אחרי [#DAYS] ימים של קריאה?>:<אם [#1:he_she] [VERB:id=read:(past_+(#1:g)+_s):#V3] במשך [#DAYS] ימים ונשארו [#1:to_him_her] עוד [#LEFT] עמודים, כמה עמודים יש בספר סך הכל?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_PAGES:sub_(#READ)):#R]>:<[NUM:value=(#READ:add_(#LEFT)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_PAGES:sub_(#DAILY)):#R]>:<[NUM:value=(#DAILY:add_(#LEFT)):#R]>]",
                "[NUM:value=(#READ):#R]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_PAGES:sub_(#DAYS)):#R]>:<[NUM:value=(#READ:sub_(#LEFT)):#R]>]",
                "[IF:(#W)=0:<הכפל את מספר העמודים היומי במספר הימים, וחסר את התוצאה מסך העמודים בספר.>:<חשב כמה עמודים נקראו עד כה (כפל), וחבר למספר העמודים שנשארו.>]"
        });

        // 5
        newTemplates.add(new String[]{
                "[PLACE:place_type=FOOD_SERVICE|EDUCATION:*:#P1][ROLE:place_id=(#P1:id);role_type=OPERATOR:*:#O1][ITEM:type=(#P1:t);unit_type=COUNT:*:#I1][HUMAN:*:#1][NUM:min=5;max=12:*:#TABLES][NUM:min=4;max=8:*:#PER_TABLE][NUM:value=(#TABLES:mul_(#PER_TABLE)):*:#ARRANGED][NUM:min=10;max=25:*:#EXTRA][NUM:min=0;max=1:*:#W][#1:n] ה[#O1:s(#1:g)] [VERB:id=arrange:(past_+(#1:g)+_s):#V1] [#TABLES] שולחנות ב[#P1:s]. על כל שולחן [#1:he_she] [VERB:id=put:(past_+(#1:g)+_s):#V2] [#PER_TABLE] [#I1:p]. [IF:(#W)=0:<בנוסף, [#1:he_she] [VERB:id=put:(past_+(#1:g)+_s):#V3] בצד עוד [#EXTRA] [#I1:p]. כמה [#I1:p] [#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s):#V4] סך הכל?>:<לפני הפתיחה, [#1:he_she] [VERB:id=take:(past_+(#1:g)+_s):#V5] [#EXTRA] [#I1:p] בחזרה למחסן. כמה [#I1:p] נשארו על השולחנות?>]",
                "[IF:(#W)=0:<[NUM:value=(#ARRANGED:add_(#EXTRA)):#R]>:<[NUM:value=(#ARRANGED:sub_(#EXTRA)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#ARRANGED:sub_(#EXTRA)):#R]>:<[NUM:value=(#ARRANGED:add_(#EXTRA)):#R]>]",
                "[NUM:value=(#ARRANGED):#R]",
                "[IF:(#W)=0:<[NUM:value=(#TABLES:add_(#EXTRA)):#R]>:<[NUM:value=(#TABLES:sub_(#EXTRA)):#R]>]",
                "[IF:(#W)=0:<חשב כמה [#I1:p] יש על השולחנות יחד (כפל), והוסף את אלו שבצד.>:<חשב כמה [#I1:p] הונחו בהתחלה (כפל), וחסר את אלו שנלקחו למחסן.>]"
        });

        // 6
        newTemplates.add(new String[]{
                "[PLACE:place_type=HOME|EDUCATION:*:#P1][ITEM:type=COLLECTIBLE|TOY|STATIONERY:*:#I1][HUMAN:*:#1][NUM:min=10;max=25:*:#BASE_START][NUM:value=(#BASE_START:mul_2):*:#START][NUM:min=5;max=12:*:#WEEKLY][NUM:min=3;max=6:*:#WEEKS][NUM:value=(#WEEKLY:mul_(#WEEKS)):*:#ADDED][NUM:min=0;max=1:*:#W]ל[#1:n] יש אוסף של [#START] [#I1:p] ב[#P1:s]. בכל שבוע [#1:he_she] [VERB:id=add:(present_+(#1:g)+_s):#V1] לאוסף עוד [#WEEKLY] [#I1:p]. [IF:(#W)=0:<כמה [#I1:p] יהיו ל[#1:n] באוסף בעוד [#WEEKS] שבועות?>:<אם [#1:he_she] [VERB:id=give:(past_+(#1:g)+_s):#V2] חצי מהאוסף ההתחלתי לחבר, כמה [#I1:p] יהיו [#1:to_him_her] סך הכל בעוד [#WEEKS] שבועות?>]",
                "[IF:(#W)=0:<[NUM:value=(#START:add_(#ADDED)):#R]>:<[NUM:value=(#START:div_2):add_(#ADDED):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#START:add_(#WEEKLY)):#R]>:<[NUM:value=(#START:div_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#START:mul_(#WEEKS)):#R]>:<[NUM:value=(#START:add_(#ADDED)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#ADDED):#R]>:<[NUM:value=(#START:sub_(#ADDED)):#R]>]",
                "[IF:(#W)=0:<הכפל את הכמות השבועית במספר השבועות, והוסף לאוסף ההתחלתי.>:<חלק את האוסף ההתחלתי ב-2, ואז הוסף את מה שנאסף במהלך השבועות.>]"
        });

        // 7
        newTemplates.add(new String[]{
                "[PLACE:place_type=OUTDOORS:*:#P1][HUMAN:*:#1][NUM:min=4;max=8:*:#KM_PER_DAY][NUM:min=3;max=7:*:#DAYS][NUM:value=(#KM_PER_DAY:mul_(#DAYS)):*:#WALKED][NUM:min=10;max=20:*:#LEFT][NUM:value=(#WALKED:add_(#LEFT)):*:#TARGET][NUM:min=0;max=1:*:#W][#1:n] [VERB:id=walk:(present_+(#1:g)+_s):#V1] במסלול הליכה ב[#P1:s]. בכל יום [#1:he_she] [VERB:id=walk:(present_+(#1:g)+_s):#V2] בדיוק [#KM_PER_DAY] קילומטרים. [IF:(#W)=0:<אם אורך המסלול הוא [#TARGET] ק\"מ, כמה ק\"מ יישארו ל[#1:n] אחרי [#DAYS] ימים של הליכה?>:<אם [#1:he_she] [VERB:id=walk:(past_+(#1:g)+_s):#V3] במשך [#DAYS] ימים, ונשארו [#1:to_him_her] עוד [#LEFT] ק\"מ לסיום, מה אורך המסלול הכולל?>]",
                "[IF:(#W)=0:<[NUM:value=(#TARGET:sub_(#WALKED)):#R]>:<[NUM:value=(#WALKED:add_(#LEFT)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TARGET:sub_(#KM_PER_DAY)):#R]>:<[NUM:value=(#WALKED:sub_(#LEFT)):#R]>]",
                "[NUM:value=(#WALKED):#R]",
                "[IF:(#W)=0:<[NUM:value=(#TARGET:sub_(#DAYS)):#R]>:<[NUM:value=(#KM_PER_DAY:add_(#LEFT)):#R]>]",
                "[IF:(#W)=0:<חשב כמה ק\"מ נהלכו סך הכל (כפל), וחסר את זה מאורך המסלול.>:<חשב כמה ק\"מ נהלכו עד כה (כפל), וחבר למספר הקילומטרים שנשארו.>]"
        });

        // 8
        newTemplates.add(new String[]{
                "[PLACE:place_type=FOOD_SERVICE|ENTERTAINMENT:*:#P1][ITEM:type=(#P1:t);unit_type=COUNT:*:#I1][ITEM:type=(#P1:t);unit_type=COUNT;id=!(#I1:id):*:#I2][HUMAN:*:#1][NUM:min=3;max=6:*:#FRIENDS][NUM:min=10;max=20:*:#P_PRICE][NUM:min=15;max=35:*:#SHARED_PRICE][NUM:value=(#FRIENDS:mul_(#P_PRICE)):*:#TOTAL_P][NUM:value=(#TOTAL_P:add_(#SHARED_PRICE)):*:#TOTAL_COST][NUM:value=200:*:#BILL][NUM:min=0;max=1:*:#W][#1:n] [VERB:id=buy:(past_+(#1:g)+_s)] [#FRIENDS] [#I1:p] ב[#P1:s] לחברים. כל [#I1:s] עולה [#P_PRICE] שקלים. [IF:(#W)=0:<בנוסף, [#1:he_she] [VERB:id=buy:(past_+(#1:g)+_s)] גם [#I2:s] עבור כולם ב-[#SHARED_PRICE] שקלים. כמה שקלים סך הכל [#1:he_she] [VERB:id=pay:(past_+(#1:g)+_s)]?>:<בנוסף, [#1:he_she] [VERB:id=buy:(past_+(#1:g)+_s)] [#I2:s] ב-[#SHARED_PRICE] שקלים, ו[VERB:id=pay:(past_+(#1:g)+_s)] לקופאי בשטר של 200 שקלים. כמה עודף מגיע [#1:to_him_her]?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST):#R]>:<[NUM:value=(#BILL:sub_(#TOTAL_COST)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_P):#R]>:<[NUM:value=(#BILL:sub_(#TOTAL_P)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST:sub_10):#R]>:<[NUM:value=(#TOTAL_COST):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_P:add_(#FRIENDS)):#R]>:<[NUM:value=(#BILL:sub_(#SHARED_PRICE)):#R]>]",
                "[IF:(#W)=0:<חשב קודם את המחיר של כל [#I1:p] יחד (כפל), ואז הוסף את המחיר של [#I2:s].>:<חשב את העלות הכוללת (כפל ואז חיבור), וחסר את התוצאה מ-200.>]"
        });

        // 9
        newTemplates.add(new String[]{
                "[PLACE:place_type=EDUCATION|HOME:*:#P1][ITEM:type=STATIONERY;unit_type=COUNT:*:#I1][HUMAN:*:#1][NUM:min=6;max=12:*:#PER_HOUR][NUM:min=3;max=6:*:#HOURS][NUM:value=(#PER_HOUR:mul_(#HOURS)):*:#BASE_DONE][NUM:min=15;max=30:*:#EXTRA][NUM:value=(#BASE_DONE:add_(#EXTRA)):*:#TOTAL_GOAL][NUM:min=0;max=1:*:#W][#1:n] [VERB:id=arrange:(present_+(#1:g)+_s)] [#I1:p] ב[#P1:s]. בכל שעה [#1:he_she] [VERB:id=arrange:(present_+(#1:g)+_s)] בדיוק [#PER_HOUR] [#I1:p]. [IF:(#W)=0:<אם [#1:he_she] [VERB:id=work:(past_+(#1:g)+_s)] במשך [#HOURS] שעות, ובערב [VERB:id=arrange:(past_+(#1:g)+_s)] עוד [#EXTRA] [#I1:p], כמה [#I1:p] [#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s)] סך הכל?>:<המטרה הייתה לסדר [#TOTAL_GOAL] [#I1:p]. אם [#1:he_she] [VERB:id=work:(past_+(#1:g)+_s)] רק במשך [#HOURS] שעות, כמה [#I1:p] עוד נשארו [#1:to_him_her] לסדר?>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE_DONE:add_(#EXTRA)):#R]>:<[NUM:value=(#TOTAL_GOAL:sub_(#BASE_DONE)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE_DONE:sub_(#EXTRA)):abs:#R]>:<[NUM:value=(#BASE_DONE:add_(#EXTRA)):#R]>]",
                "[NUM:value=(#BASE_DONE):#R]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_GOAL:sub_10):#R]>:<[NUM:value=(#TOTAL_GOAL:sub_(#HOURS)):#R]>]",
                "[IF:(#W)=0:<הכפל את קצב הסידור במספר השעות, והוסף את הכמות של הערב.>:<הכפל את קצב הסידור במספר השעות, וחסר את התוצאה מתוך המטרה הכוללת.>]"
        });

        // 10
        newTemplates.add(new String[]{
                "[PLACE:place_type=OUTDOORS|PUBLIC:*:#P1][ITEM:type=(#P1:t):*:#I1][HUMAN:*:#1][NUM:min=6;max=12:*:#ROWS][NUM:min=8;max=15:*:#PER_ROW][NUM:value=(#ROWS:mul_(#PER_ROW)):*:#TOTAL_ITEMS][NUM:min=10;max=25:*:#BROKEN][NUM:min=2;max=5:*:#EXTRA_ROWS][NUM:value=(#EXTRA_ROWS:mul_(#PER_ROW)):*:#ADDED_ITEMS][NUM:min=0;max=1:*:#W][#1:n] [VERB:id=arrange:(past_+(#1:g)+_s)] [IF:(#I1:allowed_unit)=NONE:<שורות של [#I1:p]>:<[UNIT:type=(#I1:allowed_unit);item_category=(#I1:t):p:#U1] של [#I1:p]>] ב[#P1:s]. [#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s)] [#ROWS] [IF:(#I1:allowed_unit)=NONE:<שורות>:<[#U1:p]>], ובכל [IF:(#I1:allowed_unit)=NONE:<שורה>:<[#U1:s]>] בדיוק [#PER_ROW] פריטים. [IF:(#W)=0:<לרוע המזל, [#BROKEN] פריטים נהרסו ונזרקו. כמה פריטים תקינים נשארו בסוף?>:<אחרי מנוחה, [#1:he_she] [VERB:id=add:(past_+(#1:g)+_s)] עוד [#EXTRA_ROWS] [IF:(#I1:allowed_unit)=NONE:<שורות מלאות>:<[#U1:p]>]. כמה פריטים יש עכשיו סך הכל?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_ITEMS:sub_(#BROKEN)):#R]>:<[NUM:value=(#TOTAL_ITEMS:add_(#ADDED_ITEMS)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_ITEMS:add_(#BROKEN)):#R]>:<[NUM:value=(#TOTAL_ITEMS:sub_(#ADDED_ITEMS)):#R]>]",
                "[NUM:value=(#TOTAL_ITEMS):#R]",
                "[IF:(#W)=0:<[NUM:value=(#ROWS:mul_(#BROKEN)):#R]>:<[NUM:value=(#TOTAL_ITEMS:add_(#EXTRA_ROWS)):#R]>]",
                "[IF:(#W)=0:<חשב כמה פריטים היו בהתחלה (כמות הקבוצות כפול כמות הפריטים בכל אחת), וחסר את אלו שנהרסו.>:<חשב כמה פריטים היו בהתחלה (כפל), והוסף את מספר הפריטים שנוספו לאחר מכן (כפל נוסף).>]"
        });

        // 11
        newTemplates.add(new String[]{
                "[PLACE:place_type=OUTDOORS|PUBLIC:*:#P1][HUMAN:*:#1][NUM:min=30;max=60;round=10:*:#LAP_LEN][NUM:min=3;max=6:*:#LAPS][NUM:value=(#LAP_LEN:mul_(#LAPS)):*:#TOTAL_DIST][NUM:min=20;max=50;round=10:*:#REMAINING][NUM:value=(#TOTAL_DIST:add_(#REMAINING)):*:#GOAL][NUM:min=0;max=1:*:#W][#1:n] [VERB:id=run:(present_+(#1:g)+_s)] במסלול מעגלי ב[#P1:s]. אורך כל הקפה הוא [#LAP_LEN] מטרים. [IF:(#W)=0:<אם [#1:he_she] [VERB:id=run:(past_+(#1:g)+_s)] [#LAPS] הקפות, ונשארו [#1:to_him_her] עוד [#REMAINING] מטרים לסיום האימון, מה אורך האימון הכולל במטרים?>:<המטרה של [#1:n] היא לרוץ [#GOAL] מטרים. לאחר השלמת [#LAPS] הקפות מלאות, כמה מטרים עוד נשארו [#1:to_him_her] לרוץ?>]",
                "[IF:(#W)=0:<[NUM:value=(#GOAL):#R]>:<[NUM:value=(#REMAINING):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_DIST:sub_(#REMAINING)):#R]>:<[NUM:value=(#GOAL:add_(#TOTAL_DIST)):#R]>]",
                "[NUM:value=(#TOTAL_DIST):#R]",
                "[IF:(#W)=0:<[NUM:value=(#GOAL:sub_20):#R]>:<[NUM:value=(#REMAINING:add_20):#R]>]",
                "[IF:(#W)=0:<חשב את המרחק ש[#1:he_she] כבר [VERB:id=run:(past_+(#1:g)+_s)] (מספר הקפות כפול אורך הקפה), וחבר למרחק שנשאר.>:<חשב את המרחק ש[#1:he_she] כבר [VERB:id=run:(past_+(#1:g)+_s)] (כפל), וחסר אותו מתוך יעד האימון הכולל.>]"
        });

        // 12
        newTemplates.add(new String[]{
                "[PLACE:place_type=STORE|HOME:*:#P1][ITEM:type=(#P1:t):*:#I1][HUMAN:*:#1][NUM:min=4;max=8:*:#BOXES][NUM:min=12;max=25:*:#WEIGHT_PER][NUM:value=(#BOXES:mul_(#WEIGHT_PER)):*:#TOTAL_WEIGHT][NUM:min=35;max=60:*:#EXTRA_WEIGHT][NUM:value=(#TOTAL_WEIGHT:add_(#EXTRA_WEIGHT)):*:#MAX_CAPACITY][NUM:min=0;max=1:*:#W][#1:n] [VERB:id=arrange:(past_+(#1:g)+_s)] [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[UNIT:type=(#I1:allowed_unit);item_category=(#I1:t):p:#U1] של [#I1:p]>] ב[#P1:s]. המשקל של כל [IF:(#I1:allowed_unit)=NONE:<[#I1:s]>:<[#U1:s]>] הוא בדיוק [#WEIGHT_PER] קילוגרמים. [#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s)] [#BOXES] [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[#U1:p]>] על העגלה. [IF:(#W)=0:<בנוסף, [#1:he_she] [VERB:id=add:(past_+(#1:g)+_s)] לעגלה פריט בודד ששוקל [#EXTRA_WEIGHT] ק\"ג. מה המשקל הכולל על העגלה כעת?>:<העגלה יכולה לשאת מקסימום [#MAX_CAPACITY] קילוגרמים. כמה קילוגרמים נוספים אפשר להעמיס עליה לפני שתשבר?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_WEIGHT:add_(#EXTRA_WEIGHT)):#R]>:<[NUM:value=(#MAX_CAPACITY:sub_(#TOTAL_WEIGHT)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_WEIGHT:sub_(#EXTRA_WEIGHT)):#R]>:<[NUM:value=(#MAX_CAPACITY:add_(#TOTAL_WEIGHT)):#R]>]",
                "[NUM:value=(#TOTAL_WEIGHT):#R]",
                "[IF:(#W)=0:<[NUM:value=(#MAX_CAPACITY:sub_10):#R]>:<[NUM:value=(#EXTRA_WEIGHT:add_10):#R]>]",
                "[IF:(#W)=0:<חשב את המשקל של [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[#U1:p]>] יחד (כפל), ואז הוסף את המשקל של הפריט הבודד.>:<חשב את המשקל של [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[#U1:p]>] שכבר על העגלה (כפל), וחסר אותו מהקיבולת המקסימלית.>]"
        });

        // 13
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][PLACE:place_type=STORE:*:#P1][NUM:min=10;max=30:*:#X1][NUM:value=(#X1:mul_5):*:#P1_PR][NUM:min=10;max=30:*:#X2][NUM:value=(#X2:mul_5):*:#P2_PR][NUM:value=(#P1_PR:add_(#P2_PR)):*:#SUM][NUM:value=(#SUM:div_5):*:#DISC][NUM:value=(#SUM:sub_(#DISC)):*:#FINAL][NUM:min=0;max=1:*:#W][#1:n] קנה/תה ב[#P1:s] חולצה ב-[#P1_PR] שקלים ומכנסיים ב-[#P2_PR] שקלים. בקופה הוא/היא קיבל/ה הנחה בשווי חמישית (1/5) מהסכום הכולל. [IF:(#W)=0:<כמה שקלים שילם/מה [#1:n] בסך הכל לאחר ההנחה?>:<כמה שקלים שווה ההנחה שקיבל/ה?>]",
                "[IF:(#W)=0:<[NUM:value=(#FINAL):#R]>:<[NUM:value=(#DISC):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM):#R]>:<[NUM:value=(#FINAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#P1_PR:sub_(#DISC)):#R]>:<[NUM:value=(#P2_PR:div_5):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#FINAL:add_10):#R]>:<[NUM:value=(#P1_PR:div_5):#R]>]",
                "[IF:(#W)=0:<חבר את מחירי הפריטים כדי למצוא את הסכום הכולל, חלק ב-5 כדי למצוא את ההנחה, וחסר אותה מהסכום.>:<חבר את מחירי שני הפריטים, ואת התוצאה חלק ב-5.>]"
        });

        // 14
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][ITEM:*:#I1][ITEM:*:#I2][NUM:min=3;max=6:*:#S_QTY][NUM:min=25;max=60;round=5:*:#S_PRICE][NUM:min=50;max=120;round=10:*:#P_PRICE][NUM:value=(#S_QTY:mul_(#S_PRICE)):*:#S_TOTAL][NUM:value=(#S_TOTAL:add_(#P_PRICE)):*:#TOTAL][NUM:min=0;max=1:*:#W]ל-[#1:n] היו תלושי קנייה. [#1:he_she] קנה/תה [#S_QTY] יחידות של [#I1:p] בעלות של [#S_PRICE] שקלים לכל יחידה, וגם יחידה אחת של [#I2:s] שעולה [#P_PRICE] שקלים. סך הכל הקנייה עלתה [#TOTAL] שקלים. [IF:(#W)=0:<ללא ידיעת המחיר מראש, אם נתון המחיר הכולל ומחיר ה-[#I2:s], כמה עולה [#I1:s] יחיד?>:<אם נתון המחיר הכולל, והמחיר של כל [#I1:s], כמה עלה ה-[#I2:s]?>]",
                "[IF:(#W)=0:<[NUM:value=(#S_PRICE):#R]>:<[NUM:value=(#P_PRICE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:div_(#S_QTY)):#R]>:<[NUM:value=(#S_TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#P_PRICE:div_2):#R]>:<[NUM:value=(#TOTAL:sub_(#S_PRICE)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#S_TOTAL):#R]>:<[NUM:value=(#S_PRICE:mul_2):#R]>]",
                "[IF:(#W)=0:<חסר את מחיר ה-[#I2:s] מהסכום הכולל, וחלק את מה שנשאר במספר ה-[#I1:p].>:<כפול את כמות ה-[#I1:p] במחיר שלהם, וחסר את התוצאה מהסכום הכולל.>]"
        });

        // 15
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][ITEM:*:#I1][NUM:min=5;max=12:*:#L][NUM:min=4;max=10:*:#W_VAL][NUM:min=4;max=10:*:#H][NUM:value=(#L:mul_(#W_VAL)):*:#BASE][NUM:value=(#BASE:mul_(#H)):*:#VOL][NUM:min=0;max=1:*:#W]ל-[#1:n] יש ארגז בצורת תיבה שנועד לאחסן [#I1:p]. אורך הארגז הוא [#L] ס\"מ, רוחבו [#W_VAL] ס\"מ וגובהו [#H] ס\"מ. [IF:(#W)=0:<מהו נפח הארגז בסמ\"ק?>:<אם ידוע ששטח הבסיס (אורך כפול רוחב) הוא [#BASE] סמ\"ר והנפח הוא [#VOL] סמ\"ק, מהו גובה הארגז בס\"מ?>]",
                "[IF:(#W)=0:<[NUM:value=(#VOL):#R]>:<[NUM:value=(#H):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE):#R]>:<[NUM:value=(#L:add_(#W_VAL)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#L:add_(#W_VAL)):add_(#H):#R]>:<[NUM:value=(#VOL:div_(#L)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#VOL:sub_(#H)):#R]>:<[NUM:value=(#W_VAL):#R]>]",
                "[IF:(#W)=0:<הנפח מחושב על ידי הכפלת האורך ברוחב, ואז הכפלת התוצאה בגובה.>:<כדי למצוא את הגובה, חלק את הנפח בשטח הבסיס.>]"
        });

        // 16
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][HUMAN:n=!(#1:n):*:#2][ITEM:*:#I1][NUM:min=10;max=25:*:#RA][NUM:min=10;max=25:*:#RB][NUM:value=(#RA:add_(#RB)):*:#R_SUM][NUM:min=4;max=9:*:#HOURS][NUM:value=(#R_SUM:mul_(#HOURS)):*:#TOTAL][NUM:min=0;max=1:*:#W][#1:n] ו-[#2:n] אורזים [#I1:p]. [#1:n] מסוגל/ת לארוז [#RA] [#I1:p] בשעה, ו-[#2:n] מסוגל/ת לארוז [#RB] [#I1:p] בשעה. שניהם מתחילים לעבוד ביחד באותו הזמן. [IF:(#W)=0:<אם הם יעבדו יחד במשך [#HOURS] שעות ברצף, כמה [#I1:p] הם יארזו סך הכל?>:<אם הם ארזו יחד בסך הכל [#TOTAL] [#I1:p], כמה שעות הם עבדו?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#HOURS):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#RA:mul_(#HOURS)):#R]>:<[NUM:value=(#TOTAL:div_(#RA)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#RB:mul_(#HOURS)):#R]>:<[NUM:value=(#TOTAL:div_(#RB)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#R_SUM:add_(#HOURS)):#R]>:<[NUM:value=(#HOURS:add_2):#R]>]",
                "[IF:(#W)=0:<חבר את ההספק של שניהם יחד (בשעה אחת), ואז כפול במספר השעות.>:<חבר את ההספק השעתי של שניהם יחד, ואז חלק את הכמות הכוללת בהספק המשותף.>]"
        });

        // 17
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][ITEM:*:#I1][NUM:min=2;max=4:*:#R_BLUE][NUM:min=5;max=9:*:#R_RED][NUM:min=4;max=12:*:#MULT][NUM:value=(#R_BLUE:mul_(#MULT)):*:#BLUE][NUM:value=(#R_RED:mul_(#MULT)):*:#RED][NUM:value=(#BLUE:add_(#RED)):*:#TOTAL][NUM:min=0;max=1:*:#W]באוסף ה-[#I1:p] של [#1:n], היחס בין פריטים כחולים לאדומים הוא [#R_BLUE] ל-[#R_RED] (כלומר, על כל [#R_BLUE] כחולים יש [#R_RED] אדומים). ידוע שיש לו/לה באוסף בדיוק [#RED] פריטים אדומים. [IF:(#W)=0:<כמה פריטים כחולים יש לו/לה באוסף?>:<כמה פריטים (אדומים וכחולים יחד) יש בסך הכל באוסף?>]",
                "[IF:(#W)=0:<[NUM:value=(#BLUE):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#RED:div_(#R_RED)):#R]>:<[NUM:value=(#BLUE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#RED:sub_(#R_RED)):#R]>:<[NUM:value=(#TOTAL:sub_(#R_BLUE)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#R_BLUE:mul_2):#R]>:<[NUM:value=(#RED:mul_2):#R]>]",
                "[IF:(#W)=0:<בדוק פי כמה גדול מספר האדומים מהיחס שלהם, והכפל את היחס של הכחולים באותו מספר.>:<מצא קודם את כמות הפריטים הכחולים (בעזרת מכפיל היחס), ואז חבר אותם לאדומים.>]"
        });

        // 18
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][HUMAN:n=!(#1:n):*:#2][HUMAN:n=!(#1:n);n=!(#2:n):*:#3][NUM:min=3;max=10:*:#MULT][NUM:value=(#MULT:mul_12):*:#TOTAL][NUM:value=(#TOTAL:div_3):*:#A_SHARE][NUM:value=(#TOTAL:div_4):*:#B_SHARE][NUM:value=(#A_SHARE:add_(#B_SHARE)):*:#SUM_AB][NUM:value=(#TOTAL:sub_(#SUM_AB)):*:#LEFT][NUM:min=0;max=1:*:#W]על השולחן היו [#TOTAL] עוגיות. [#1:n] אכל/ה בדיוק שליש (1/3) מהן, ו-[#2:n] אכל/ה בדיוק רבע (1/4) מכלל העוגיות שהיו בהתחלה. שאר העוגיות נשארו ל-[#3:n]. [IF:(#W)=0:<כמה עוגיות אכלו [#1:n] ו-[#2:n] ביחד?>:<כמה עוגיות נשארו בסוף ל-[#3:n]?>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM_AB):#R]>:<[NUM:value=(#LEFT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#LEFT):#R]>:<[NUM:value=(#SUM_AB):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#A_SHARE):#R]>:<[NUM:value=(#TOTAL:sub_(#A_SHARE)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#B_SHARE):#R]>:<[NUM:value=(#TOTAL:sub_(#B_SHARE)):#R]>]",
                "[IF:(#W)=0:<חשב שליש מהסכום הכולל (חלק ב-3), חשב רבע מהסכום (חלק ב-4), וחבר את התוצאות.>:<מצא כמה עוגיות נאכלו על ידי שניהם יחד, וחסר את התוצאה מהכמות ההתחלתית.>]"
        });

        // 19
        newTemplates.add(new String[]{
                "[PLACE:*:#P1][NUM:min=5;max=15:*:#W_VAL][NUM:value=(#W_VAL:mul_3):*:#L_VAL][NUM:value=(#W_VAL:add_(#L_VAL)):*:#HALF][NUM:value=(#HALF:mul_2):*:#PERIM][NUM:value=(#W_VAL:mul_(#L_VAL)):*:#AREA][NUM:min=0;max=1:*:#W]ב[#P1:s] תכננו חלקה מלבנית. נתון כי אורך החלקה גדול פי 3 מרוחבה. רוחב החלקה הוא בדיוק [#W_VAL] מטרים. [IF:(#W)=0:<מהו ההיקף הכולל של החלקה במטרים?>:<מהו שטח החלקה במ\"ר?>]",
                "[IF:(#W)=0:<[NUM:value=(#PERIM):#R]>:<[NUM:value=(#AREA):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#HALF):#R]>:<[NUM:value=(#PERIM):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AREA):#R]>:<[NUM:value=(#W_VAL:mul_4):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#L_VAL:mul_2):#R]>:<[NUM:value=(#L_VAL):#R]>]",
                "[IF:(#W)=0:<קודם מצא את האורך (כפול את הרוחב ב-3). ההיקף הוא סכום כל 4 הצלעות (פעמיים אורך ופעמיים רוחב).>:<קודם מצא את האורך, והשטח שווה לאורך כפול הרוחב.>]"
        });

        // 20
        newTemplates.add(new String[]{
                "[NUM:min=4;max=8:*:#S][NUM:min=3;max=6:*:#P][NUM:min=2;max=5:*:#SH][NUM:value=(#S:mul_(#P)):*:#SP][NUM:value=(#SP:mul_(#SH)):*:#COMBO][NUM:min=0;max=1:*:#W]במסעדה יש תפריט עסקי המאפשר להרכיב ארוחה הכוללת מנה ראשונה, עיקרית וקינוח. אפשר לבחור מתוך [#S] מנות ראשונות, [#P] מנות עיקריות, ו-[#SH] קינוחים שונים. [IF:(#W)=0:<כמה ארוחות שונות לחלוטין (קומבינציות) אפשר להרכיב מהתפריט?>:<אם אדם היה מוותר על הקינוח ובוחר רק מנה ראשונה ועיקרית, כמה ארוחות שונות הוא יכול היה להרכיב?>]",
                "[IF:(#W)=0:<[NUM:value=(#COMBO):#R]>:<[NUM:value=(#SP):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SP):#R]>:<[NUM:value=(#COMBO):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#S:add_(#P)):add_(#SH):#R]>:<[NUM:value=(#S:add_(#P)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SP:add_(#SH)):#R]>:<[NUM:value=(#P:mul_(#SH)):#R]>]",
                "[IF:(#W)=0:<כדי למצוא את מספר האפשרויות הכולל, עליך להכפיל את מספר האפשרויות של כל מנה זו בזו.>:<הכפל רק את מספר המנות הראשונות במספר המנות העיקריות.>]"
        });

        // 21
        newTemplates.add(new String[]{
                "[NUM:min=60;max=90;round=10:*:#V1][NUM:min=60;max=90;round=10:*:#V2][NUM:min=2;max=5:*:#T][NUM:value=(#V1:mul_(#T)):*:#D1][NUM:value=(#V2:mul_(#T)):*:#D2][NUM:value=(#D1:add_(#D2)):*:#TOTAL][NUM:min=0;max=1:*:#W]שתי רכבות יצאו מאותה תחנה בדיוק באותו הזמן ונסעו בכיוונים מנוגדים. רכבת א' נסעה במהירות קבועה של [#V1] קמ\"ש, ורכבת ב' נסעה במהירות קבועה של [#V2] קמ\"ש. הן נסעו ברצף במשך [#T] שעות. [IF:(#W)=0:<מהו המרחק בקילומטרים ביניהן לאחר [#T] שעות?>:<איזה מרחק (בקילומטרים) עברה רק רכבת א' לבדה?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#D1):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#D1):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#D2):#R]>:<[NUM:value=(#V1:add_(#T)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#V1:add_(#V2)):#R]>:<[NUM:value=(#D2):#R]>]",
                "[IF:(#W)=0:<חשב את המרחק שעברה כל רכבת בנפרד (מהירות כפול זמן), וחבר את שני המרחקים יחד.>:<כדי למצוא מרחק, כפול את המהירות של הרכבת בזמן הנסיעה שלה.>]"
        });

        // 22
        newTemplates.add(new String[]{
                "[NUM:min=30;max=60:*:#AVG][NUM:min=20;max=40:*:#X][NUM:min=20;max=40:*:#Y][NUM:value=(#AVG:mul_3):*:#SUM][NUM:value=(#X:add_(#Y)):*:#XY][NUM:value=(#SUM:sub_(#XY)):*:#Z][NUM:min=0;max=1:*:#W]הממוצע החשבוני של שלושה מספרים שונה הוא בדיוק [#AVG]. ידוע שהמספר הראשון הוא [#X] והמספר השני הוא [#Y]. [IF:(#W)=0:<מהו המספר השלישי?>:<מהו סכום שלושת המספרים יחד (לפני החלוקה בממוצע)?>]",
                "[IF:(#W)=0:<[NUM:value=(#Z):#R]>:<[NUM:value=(#SUM):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM):#R]>:<[NUM:value=(#Z):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AVG:sub_(#X)):#R]>:<[NUM:value=(#XY):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#XY:sub_(#AVG)):abs:#R]>:<[NUM:value=(#SUM:add_10):#R]>]",
                "[IF:(#W)=0:<כדי למצוא את המספר השלישי, מצא את הסכום של שלושתם (הממוצע כפול 3), וחסר ממנו את שני המספרים הידועים.>:<סכום המספרים שווה לממוצע שלהם כפול כמות המספרים (3).>]"
        });

        // 23
        newTemplates.add(new String[]{
                "[PLACE:*:#P1][NUM:min=4;max=10:*:#A][NUM:min=5;max=12:*:#B][NUM:min=8;max=20:*:#C][NUM:value=(#A:mul_(#B)):*:#AB][NUM:value=(#AB:mul_(#C)):*:#TOTAL][NUM:min=0;max=1:*:#W]ב[#P1:s] הגדול יש [#A] מתחמים שונים. בכל מתחם יש [#B] שורות של מדפים, ובכל שורה כזו מונחים בדיוק [#C] ארגזים. [IF:(#W)=0:<כמה ארגזים יש בסך הכל ב[#P1:s]?>:<כמה שורות של מדפים יש בסך הכל בכל המתחמים יחד?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#AB):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AB):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#A:add_(#B)):add_(#C):#R]>:<[NUM:value=(#A:add_(#B)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#B:mul_(#C)):#R]>:<[NUM:value=(#B:add_10):#R]>]",
                "[IF:(#W)=0:<הכפל את מספר המתחמים במספר השורות, ואת התוצאה במספר הארגזים בכל שורה.>:<הכפל רק את מספר המתחמים במספר השורות שבכל מתחם.>]"
        });

        // 24
        newTemplates.add(new String[]{
                "[NUM:min=200;max=500;round=10:*:#P][NUM:min=30;max=80;round=10:*:#X][NUM:min=20;max=60;round=10:*:#Y][NUM:value=(#X:add_(#Y)):*:#SAVED][NUM:value=(#P:sub_(#SAVED)):*:#FINAL][NUM:min=0;max=1:*:#W]מחירו ההתחלתי של מכשיר היה [#P] שקלים. ביום ראשון הוכרז מבצע והמחיר ירד ב-[#X] שקלים. ביום שלישי המחיר ירד שוב, הפעם ב-[#Y] שקלים נוספים. [IF:(#W)=0:<מהו המחיר הסופי של המכשיר לאחר שתי ההוזלות?>:<בכמה שקלים סך הכל ירד המחיר מההתחלה ועד הסוף?>]",
                "[IF:(#W)=0:<[NUM:value=(#FINAL):#R]>:<[NUM:value=(#SAVED):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SAVED):#R]>:<[NUM:value=(#FINAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#P:sub_(#X)):#R]>:<[NUM:value=(#P:sub_(#SAVED)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#FINAL:add_10):#R]>:<[NUM:value=(#SAVED:sub_10):#R]>]",
                "[IF:(#W)=0:<חסר מהמחיר ההתחלתי את ההוזלה הראשונה, וממה שנשאר חסר את ההוזלה השנייה.>:<חבר יחד את סכומי שתי ההוזלות (הראשונה והשנייה).>]"
        });

        // 25
        newTemplates.add(new String[]{
                "[NUM:min=15;max=40:*:#X][NUM:min=1;max=3:*:#H][NUM:min=10;max=40:*:#M][NUM:value=(#H:mul_60):*:#H_MIN][NUM:value=(#H_MIN:add_(#M)):*:#MINUTES][NUM:value=(#X:mul_(#MINUTES)):*:#TOTAL][NUM:min=0;max=1:*:#W]מכונת ייצור מוציאה [#X] בקבוקים בכל דקה של עבודה. ידוע שהמכונה עבדה ברצף במשך [#H] שעות ו-[#M] דקות. [IF:(#W)=0:<כמה בקבוקים ייצרה המכונה סך הכל בפרק הזמן הזה?>:<כמה דקות סך הכל עבדה המכונה (המרת השעות והדקות לדקות בלבד)?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#MINUTES):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#MINUTES):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#H_MIN:mul_(#X)):#R]>:<[NUM:value=(#H_MIN):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#X:mul_(#H)):add_(#M):#R]>:<[NUM:value=(#H:add_(#M)):#R]>]",
                "[IF:(#W)=0:<המר תחילה את זמן העבודה לדקות בלבד (כל שעה היא 60 דקות), ואז כפול במספר הבקבוקים לדקה.>:<הכפל את מספר השעות ב-60 כדי להמיר לדקות, ואז הוסף את הדקות הנותרות.>]"
        });

        // 26
        newTemplates.add(new String[]{
                "[HUMAN:g=m:*:#1][NUM:min=8;max=15:*:#A][NUM:value=(#A:mul_2):*:#B][NUM:min=3;max=6:*:#Y][NUM:value=(#B:sub_(#Y)):*:#C][NUM:value=(#A:add_(#B)):add_(#C):*:#TOTAL][NUM:min=0;max=1:*:#W]גילו של [#1:n] הוא בדיוק [#A]. ידוע שגילו של אחיו הגדול הוא פי 2 מגילו של [#1:n]. כמו כן, אחותם הקטנה צעירה מהאח הגדול ב-[#Y] שנים. [IF:(#W)=0:<מהו סכום הגילים של שלושתם יחד כיום?>:<בת כמה האחות הקטנה כיום?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#C):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#C):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#A:add_(#B)):#R]>:<[NUM:value=(#B):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_2):#R]>:<[NUM:value=(#C:add_2):#R]>]",
                "[IF:(#W)=0:<חשב את הגיל של האח הגדול, לאחר מכן את גיל האחות (חסר מהגדול), ולבסוף חבר את כל השלושה.>:<חשב את הגיל של האח הגדול (כפול 2 מהגיל הראשון), וחסר ממנו את ההפרש.>]"
        });

        // 27
        newTemplates.add(new String[]{
                "[HUMAN:g=f:*:#1][NUM:min=100;max=250;round=10:*:#HALF][NUM:value=(#HALF:mul_2):*:#M][NUM:min=30;max=80;round=10:*:#X][NUM:value=(#HALF:sub_(#X)):*:#LEFT][NUM:value=(#HALF:add_(#X)):*:#SPENT][NUM:min=0;max=1:*:#W]ל-[#1:n] היו בארנק [#M] שקלים. היא הוציאה בדיוק חצי מהסכום על זוג נעליים, ולאחר מכן הוציאה עוד [#X] שקלים על ספר. [IF:(#W)=0:<כמה שקלים נשארו לה בארנק בסוף?>:<כמה שקלים היא הוציאה סך הכל בשתי הקניות יחד?>]",
                "[IF:(#W)=0:<[NUM:value=(#LEFT):#R]>:<[NUM:value=(#SPENT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SPENT):#R]>:<[NUM:value=(#LEFT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#HALF):#R]>:<[NUM:value=(#M:sub_(#X)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#LEFT:add_10):#R]>:<[NUM:value=(#SPENT:sub_10):#R]>]",
                "[IF:(#W)=0:<חלק את הסכום ההתחלתי ב-2, ואז חסר מהתוצאה את מחיר הספר.>:<חלק את הסכום ההתחלתי ב-2 (מחיר הנעליים), ואז חבר אליו את מחיר הספר.>]"
        });

        // 28
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][ITEM:type=MONEY|ENTERTAINMENT:*:#I1][NUM:min=100;max=500;round=100:*:#WHOLE][NUM:min=10;max=50;round=10:*:#PERCENT][NUM:value=(#WHOLE:mul_(#PERCENT)):div_100:*:#PART][NUM:min=0;max=1:*:#W]ל-[#1:n] היו [#WHOLE] [#I1:p]. [#1:he_she] החליט/ה לתרום בדיוק [#PERCENT] אחוזים (%) מהם. [IF:(#W)=0:<כמה [#I1:p] תרם/מה [#1:n] בסך הכל?>:<אם נתון ש-[#1:n] תרם/מה בדיוק [#PART] [#I1:p], והם היוו [#PERCENT] אחוזים מסך כל ה-[#I1:p] שהיו לו/לה, כמה היו לו/לה בהתחלה?>]",
                "[IF:(#W)=0:<[NUM:value=(#PART):#R]>:<[NUM:value=(#WHOLE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#WHOLE:sub_(#PART)):#R]>:<[NUM:value=(#PART:mul_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#WHOLE:div_10):#R]>:<[NUM:value=(#WHOLE:sub_(#PART)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PART:add_10):#R]>:<[NUM:value=(#WHOLE:add_100):#R]>]",
                "[IF:(#W)=0:<כדי לחשב אחוזים, כפול את הכמות הכוללת באחוז שניתן, וחלק ב-100.>:<כדי למצוא את השלם, חלק את הכמות שנתרמה באחוז, וכפול ב-100.>]"
        });

        // 29
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][HUMAN:n=!(#1:n):*:#2][ITEM:*:#I1][NUM:min=2;max=5:*:#A][NUM:min=3;max=7:*:#B][NUM:value=(#A:add_(#B)):*:#SUM_R][NUM:min=5;max=12:*:#MULT][NUM:value=(#SUM_R:mul_(#MULT)):*:#TOTAL][NUM:value=(#A:mul_(#MULT)):*:#SHARE_A][NUM:min=0;max=1:*:#W][#1:n] ו-[#2:n] חילקו ביניהם אוסף של [#TOTAL] [#I1:p]. היחס בחלוקה היה [#A] ל-[#1:n] על כל [#B] ל-[#2:n] (יחס של [#A]:[#B]). [IF:(#W)=0:<כמה [#I1:p] קיבל/ה [#1:n] בסוף החלוקה?>:<כמה חלקים (מנות יחס) היו בסך הכל בחלוקה הזו (הסכום של חלקי היחס)?>]",
                "[IF:(#W)=0:<[NUM:value=(#SHARE_A):#R]>:<[NUM:value=(#SUM_R):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:div_(#SUM_R)):#R]>:<[NUM:value=(#A:mul_(#B)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_(#SHARE_A)):#R]>:<[NUM:value=(#MULT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SHARE_A:add_5):#R]>:<[NUM:value=(#A):#R]>]",
                "[IF:(#W)=0:<חבר את שני מספרי היחס, חלק את הכמות הכוללת בסכום הזה כדי למצוא כמה שווה \"חלק\" אחד, ואז כפול במספר החלקים של [#1:n].>:<פשוט חבר את שני מספרי היחס המופיעים בשאלה.>]"
        });

        // 30
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][ITEM:type=FOOD:*:#I1][NUM:min=4;max=10:*:#DENOM][NUM:min=2;max=(#DENOM:sub_1):*:#NUMER][NUM:min=5;max=12:*:#MULT][NUM:value=(#DENOM:mul_(#MULT)):*:#TOTAL][NUM:value=(#NUMER:mul_(#MULT)):*:#PART][NUM:value=(#TOTAL:sub_(#PART)):*:#LEFT][NUM:min=0;max=1:*:#W]ל-[#1:n] קופסה עם [#TOTAL] [#I1:p]. [#1:he_she] חילק/ה לחברים בדיוק [#NUMER] חלקי [#DENOM] (שבר: [#NUMER]/[#DENOM]) מכל ה-[#I1:p]. [IF:(#W)=0:<כמה [#I1:p] קיבלו החברים?>:<כמה [#I1:p] נשארו בקופסה של [#1:n]?>]",
                "[IF:(#W)=0:<[NUM:value=(#PART):#R]>:<[NUM:value=(#LEFT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#LEFT):#R]>:<[NUM:value=(#PART):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:div_(#DENOM)):#R]>:<[NUM:value=(#TOTAL:div_(#DENOM)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_(#DENOM)):#R]>:<[NUM:value=(#TOTAL:sub_(#NUMER)):#R]>]",
                "[IF:(#W)=0:<חלק את הכמות הכוללת במכנה (למטה) כדי למצוא כמה שווה חלק אחד, ואז כפול במונה (למעלה).>:<מצא קודם כמה פריטים חולקו (חילוק במכנה וכפל במונה), ואז חסר את התוצאה מהכמות ההתחלתית.>]"
        });

        // 31
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][NUM:min=5;max=20:*:#SCALE][NUM:min=4;max=15:*:#MAP_D][NUM:value=(#SCALE:mul_(#MAP_D)):*:#REAL_D][NUM:min=0;max=1:*:#W][#1:n] מסתכל/ת על מפה. קנה המידה מראה שכל 1 ס\"מ במפה שווה ל-[#SCALE] קילומטרים במציאות. [IF:(#W)=0:<אם המרחק בין שתי ערים במפה הוא [#MAP_D] ס\"מ, מהו המרחק ביניהן במציאות בקילומטרים?>:<אם המרחק האמיתי בין שני הרים הוא [#REAL_D] קילומטרים, כמה ס\"מ יפרידו ביניהם על המפה?>]",
                "[IF:(#W)=0:<[NUM:value=(#REAL_D):#R]>:<[NUM:value=(#MAP_D):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#REAL_D:add_10):#R]>:<[NUM:value=(#MAP_D:add_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SCALE:add_(#MAP_D)):#R]>:<[NUM:value=(#REAL_D:sub_(#SCALE)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SCALE:mul_10):#R]>:<[NUM:value=(#REAL_D:div_10):#R]>]",
                "[IF:(#W)=0:<כפול את המרחק בס\"מ על המפה בקנה המידה שניתן.>:<חלק את המרחק האמיתי במספר הקילומטרים שכל ס\"מ מייצג (קנה המידה).>]"
        });

        // 32
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][NUM:min=10;max=20:*:#P1][NUM:min=3;max=6:*:#D1][NUM:value=(#P1:mul_(#D1)):*:#PART1][NUM:min=25;max=40:*:#P2][NUM:min=2;max=5:*:#D2][NUM:value=(#P2:mul_(#D2)):*:#PART2][NUM:value=(#PART1:add_(#PART2)):*:#TOTAL][NUM:min=0;max=1:*:#W][#1:n] קורא/ת ספר. בחלק הראשון, [#1:he_she] קרא/ה [#P1] עמודים בכל יום במשך [#D1] ימים. בחלק השני, [#1:he_she] הגביר/ה את הקצב וקרא/ה [#P2] עמודים בכל יום במשך [#D2] ימים נוספים. [IF:(#W)=0:<כמה עמודים קרא/ה [#1:n] סך הכל בכל הימים?>:<אם הספר מכיל [#TOTAL] עמודים ו-[#1:n] סיים/מה אותו בדיוק כך, כמה עמודים קרא/ה רק בחלק השני (בימים האחרונים)?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#PART2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PART1):#R]>:<[NUM:value=(#PART1):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#P1:add_(#P2)):mul_(#D1:add_(#D2)):#R]>:<[NUM:value=(#TOTAL:sub_(#P1)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_10):#R]>:<[NUM:value=(#PART2:add_10):#R]>]",
                "[IF:(#W)=0:<חשב כמה עמודים נקראו בחלק הראשון (כפל), הוסף את העמודים שנקראו בחלק השני (כפל נוסף).>:<כפול את קצב הקריאה של החלק השני במספר הימים של החלק השני.>]"
        });

        // 33
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][NUM:min=200;max=500;round=10:*:#MID][NUM:min=100;max=300;round=10:*:#W1][NUM:value=(#MID:add_(#W1)):*:#S][NUM:min=50;max=200;round=10:*:#D1][NUM:value=(#MID:add_(#D1)):*:#FINAL][NUM:min=0;max=1:*:#W]ל-[#1:n] היה סכום כסף מסוים בקופה. [#1:he_she] הוציא/ה מהקופה [#W1] שקלים. למחרת, [#1:he_she] הוסיף/פה לקופה [#D1] שקלים. כעת יש בקופה בדיוק [#FINAL] שקלים. [IF:(#W)=0:<כמה שקלים היו ל-[#1:n] בקופה לפני כל הפעולות (בהתחלה)?>:<אם נתעלם מהסכום שהוסיף/פה, כמה שקלים היו לו/לה בקופה מיד לאחר ההוצאה ביום הראשון?>]",
                "[IF:(#W)=0:<[NUM:value=(#S):#R]>:<[NUM:value=(#MID):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#FINAL:add_(#D1)):sub_(#W1):#R]>:<[NUM:value=(#S):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#MID):#R]>:<[NUM:value=(#FINAL:add_(#D1)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#S:add_100):#R]>:<[NUM:value=(#MID:sub_50):#R]>]",
                "[IF:(#W)=0:<עבוד מהסוף להתחלה: חסר את הסכום שהוסף מהסכום הסופי, ואז הוסף בחזרה את מה שהוצא.>:<פשוט חסר את הסכום שהוסף ביום השני מהסכום הסופי.>]"
        });

        // 34
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][PLACE:place_type=OUTDOORS:*:#P1][NUM:min=5;max=15:*:#W_VAL][NUM:min=3;max=8:*:#DIFF][NUM:value=(#W_VAL:add_(#DIFF)):*:#L_VAL][NUM:value=(#L_VAL:add_(#W_VAL)):*:#HALF][NUM:value=(#HALF:mul_2):*:#PERIM][NUM:min=0;max=1:*:#W][#1:n] מגדר/ת שטח מלבני ב[#P1:s]. רוחב השטח הוא [#W_VAL] מטרים, והאורך שלו גדול מהרוחב ב-[#DIFF] מטרים. [IF:(#W)=0:<כמה מטרים של גדר צריך סך הכל כדי להקיף את כל המלבן (מהו ההיקף)?>:<מהו אורך המלבן במטרים (הצלע הארוכה)?>]",
                "[IF:(#W)=0:<[NUM:value=(#PERIM):#R]>:<[NUM:value=(#L_VAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#HALF):#R]>:<[NUM:value=(#PERIM):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#L_VAL:mul_(#W_VAL)):#R]>:<[NUM:value=(#DIFF):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PERIM:sub_(#DIFF)):#R]>:<[NUM:value=(#L_VAL:add_2):#R]>]",
                "[IF:(#W)=0:<חשב את האורך קודם. לאחר מכן, חבר את האורך והרוחב, והכפל את התוצאה ב-2 כדי למצוא את ההיקף המלא.>:<כדי למצוא את האורך, פשוט הוסף לרוחב הנתון את ההפרש.>]"
        });

        // 35
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][HUMAN:n=!(#1:n):*:#2][NUM:min=12;max=18:*:#AGE1][NUM:min=8;max=11:*:#AGE2][NUM:min=5;max=10:*:#Y][NUM:value=(#AGE1:add_(#AGE2)):*:#SUM_NOW][NUM:value=(#Y:mul_2):*:#ADDED_YEARS][NUM:value=(#SUM_NOW:add_(#ADDED_YEARS)):*:#SUM_FUT][NUM:min=0;max=1:*:#W][#1:n] כיום בן/בת [#AGE1], ו-[#2:n] בן/בת [#AGE2]. [IF:(#W)=0:<בעוד [#Y] שנים, מה יהיה סכום הגילים של שניהם יחד?>:<בעוד כמה שנים מהיום סכום הגילים של שניהם יחד יהיה בדיוק [#SUM_FUT]?>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM_FUT):#R]>:<[NUM:value=(#Y):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM_NOW:add_(#Y)):#R]>:<[NUM:value=(#ADDED_YEARS):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM_NOW):#R]>:<[NUM:value=(#SUM_FUT:sub_(#SUM_NOW)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM_FUT:add_2):#R]>:<[NUM:value=(#Y:add_1):#R]>]",
                "[IF:(#W)=0:<חשב את סכום הגילים שלהם היום, וזכור להוסיף [#Y] שנים *לכל אחד מהם* (כלומר פעמיים [#Y]).>:<מצא את ההפרש בין הסכום העתידי לסכום שלהם היום, וחלק ב-2 (כי שניהם מתבגרים יחד).>]"
        });

        // 36
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][PLACE:place_type=STORE:*:#P1][ITEM:type=(#P1:t):*:#I1][ITEM:type=(#P1:t);id=!(#I1:id):*:#I2][NUM:min=15;max=30:*:#PRICE_A][NUM:min=4;max=8:*:#QTY][NUM:min=40;max=80:*:#PRICE_B][NUM:value=(#PRICE_A:mul_(#QTY)):*:#TOTAL_A][NUM:value=(#TOTAL_A:add_(#PRICE_B)):*:#TOTAL][NUM:min=0;max=1:*:#W][#1:n] עורכ/ת קניות ב[#P1:s]. [#1:he_she] לקח/ה [#QTY] יחידות של [#I1:p], שכל אחת עולה [#PRICE_A] שקלים. בנוסף, [#1:he_she] לקח/ה יחידה אחת בלבד של [#I2:s] שעולה [#PRICE_B] שקלים. [IF:(#W)=0:<כמה שילם/מה [#1:n] סך הכל בקופה?>:<אם המחיר הכולל היה [#TOTAL] שקלים, ומחיר היחידה הבודדת של ה-[#I2:s] היה [#PRICE_B] שקלים, כמה עלו כל ה-[#I1:p] יחד?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#TOTAL_A):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_A):#R]>:<[NUM:value=(#PRICE_A):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PRICE_A:add_(#PRICE_B)):mul_(#QTY):#R]>:<[NUM:value=(#TOTAL:div_(#QTY)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_10):#R]>:<[NUM:value=(#TOTAL_A:add_20):#R]>]",
                "[IF:(#W)=0:<הכפל את כמות הפריטים הזהים במחירם, ואז חבר לסכום את מחיר הפריט הבודד השונה.>:<פשוט חסר את מחיר הפריט השונה מהסכום הכולל.>]"
        });

        // 37
        newTemplates.add(new String[]{
                "[NUM:min=20;max=40;round=10:*:#V1][NUM:min=30;max=50;round=10:*:#V2][NUM:min=2;max=5:*:#T][NUM:value=(#V1:add_(#V2)):*:#SUM_V][NUM:value=(#SUM_V:mul_(#T)):*:#DIST][NUM:min=0;max=1:*:#W]שני רוכבי אופניים יצאו בו זמנית משתי ערים שונות ורכבו זה לקראת זה. הרוכב הראשון נסע במהירות של [#V1] קמ\"ש והרוכב השני במהירות של [#V2] קמ\"ש. הם נפגשו בדיוק לאחר [#T] שעות. [IF:(#W)=0:<מה המרחק בקילומטרים בין שתי הערים?>:<מה הייתה המהירות המשותפת שלהם יחד (סכום המהירויות) בקמ\"ש?>]",
                "[IF:(#W)=0:<[NUM:value=(#DIST):#R]>:<[NUM:value=(#SUM_V):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM_V):#R]>:<[NUM:value=(#DIST):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#V1:mul_(#T)):#R]>:<[NUM:value=(#SUM_V:mul_(#T)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#V2:mul_(#T)):#R]>:<[NUM:value=(#V2:sub_(#V1)):#R]>]",
                "[IF:(#W)=0:<חבר את המהירויות של שני הרוכבים כדי למצוא את קצב ההתקרבות שלהם בשעה, ואז כפול בזמן שחלף עד הפגישה.>:<פשוט חבר את המהירות של הרוכב הראשון למהירות של הרוכב השני.>]"
        });

        // 38
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][ITEM:type=FOOD|TOY:*:#I1][NUM:min=3;max=6:*:#QTY1][NUM:min=10;max=20:*:#W1][NUM:value=(#QTY1:mul_(#W1)):*:#TOT1][NUM:min=2;max=4:*:#QTY2][NUM:min=25;max=40:*:#W2][NUM:value=(#QTY2:mul_(#W2)):*:#TOT2][NUM:value=(#TOT1:add_(#TOT2)):*:#TOTAL_W][NUM:min=0;max=1:*:#W][#1:n] מעמיס/ה ארגזים של [#I1:p]. ישנם [#QTY1] ארגזים קטנים ששוקלים [#W1] ק\"ג כל אחד, ועוד [#QTY2] ארגזים גדולים ששוקלים [#W2] ק\"ג כל אחד. [IF:(#W)=0:<מהו המשקל הכולל של כל הארגזים יחד בקילוגרמים?>:<מהו המשקל הכולל רק של הארגזים הגדולים יחד בקילוגרמים?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_W):#R]>:<[NUM:value=(#TOT2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOT1):#R]>:<[NUM:value=(#TOTAL_W):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#QTY1:add_(#QTY2)):mul_(#W1:add_(#W2)):#R]>:<[NUM:value=(#TOT1):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_W:sub_10):#R]>:<[NUM:value=(#TOT2:add_5):#R]>]",
                "[IF:(#W)=0:<חשב את משקל הארגזים הקטנים יחד, חשב את משקל הארגזים הגדולים יחד, וחבר את התוצאות.>:<הכפל את כמות הארגזים הגדולים במשקל של ארגז גדול בודד.>]"
        });

        // 39
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][NUM:min=3;max=6:*:#C1][NUM:min=2;max=5:*:#C2][NUM:min=2;max=4:*:#C3][NUM:value=(#C1:mul_(#C2)):mul_(#C3):*:#COMBO][NUM:value=(#C3:sub_1):*:#NEW_C3][NUM:value=(#C1:mul_(#C2)):mul_(#NEW_C3):*:#COMBO_NEW][NUM:min=0;max=1:*:#W]ל-[#1:n] יש בארון [#C1] חולצות שונות, [#C2] זוגות מכנסיים שונים, ו-[#C3] זוגות נעליים. [IF:(#W)=0:<כמה תלבושות שונות לחלוטין (הכוללות חולצה, מכנסיים ונעליים) ניתן להרכיב?>:<אם זוג נעליים אחד אבד, כמה תלבושות שונות לחלוטין (הכוללות חולצה, מכנסיים ונעליים) אפשר להרכיב עכשיו עם מה שנשאר?>]",
                "[IF:(#W)=0:<[NUM:value=(#COMBO):#R]>:<[NUM:value=(#COMBO_NEW):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#C1:add_(#C2)):add_(#C3):#R]>:<[NUM:value=(#COMBO):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#C1:mul_(#C2)):#R]>:<[NUM:value=(#COMBO:sub_1):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#COMBO:sub_2):#R]>:<[NUM:value=(#C1:add_(#C2)):add_(#NEW_C3):#R]>]",
                "[IF:(#W)=0:<כדי למצוא את מספר האפשרויות הכללי, יש להכפיל את מספר האפשרויות מכל סוג אלו באלו (חולצות * מכנסיים * נעליים).>:<חסר קודם זוג נעליים אחד מהסך הכל, ואז הכפל את הכמויות (חולצות * מכנסיים * נעליים שנשארו).>]"
        });

        // 40
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][ITEM:type=TOY|FOOD:*:#I1][NUM:min=4;max=8:*:#DIVISOR][NUM:min=10;max=20:*:#QUOTIENT][NUM:value=(#DIVISOR:mul_(#QUOTIENT)):*:#LEFT][NUM:min=15;max=40:*:#GIVEN][NUM:value=(#LEFT:add_(#GIVEN)):*:#TOTAL][NUM:min=0;max=1:*:#W]ל-[#1:n] היו בהתחלה [#TOTAL] [#I1:p]. תחילה, [#1:he_she] חילק/ה [#GIVEN] [#I1:p] לאחיו/לאחותו. לאחר מכן, את מה שנשאר, [#1:he_she] חילק/ה שווה בשווה לתוך [#DIVISOR] קופסאות, ולא נשאר כלום בחוץ. [IF:(#W)=0:<כמה [#I1:p] הונחו בתוך כל קופסה?>:<כמה [#I1:p] נשארו ל-[#1:n] לפני החלוקה לקופסאות (מיד לאחר שנתן/נה לאחיו/לאחותו)?>]",
                "[IF:(#W)=0:<[NUM:value=(#QUOTIENT):#R]>:<[NUM:value=(#LEFT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:div_(#DIVISOR)):#R]>:<[NUM:value=(#QUOTIENT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#LEFT):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#QUOTIENT:add_2):#R]>:<[NUM:value=(#LEFT:add_5):#R]>]",
                "[IF:(#W)=0:<חסר מהכמות ההתחלתית את הפריטים שחולקו, ואת התוצאה חלק במספר הקופסאות.>:<פשוט חסר את הפריטים שניתנו במתנה מהסכום שהיה בהתחלה.>]"
        });

        // 41
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][NUM:min=5;max=12:*:#HOURS][NUM:min=30;max=50;round=5:*:#RATE][NUM:value=(#HOURS:mul_(#RATE)):*:#BASE][NUM:min=50;max=100;round=10:*:#BONUS][NUM:value=(#BASE:add_(#BONUS)):*:#TOTAL][NUM:min=0;max=1:*:#W][#1:n] עובד/ת ומרוויח/ה [#RATE] שקלים על כל שעת עבודה. בנוסף, בסוף השבוע הוא/היא קיבל/ה בונוס קבוע של [#BONUS] שקלים. ידוע שבאותו שבוע [#1:he_she] עבד/ה בדיוק [#HOURS] שעות. [IF:(#W)=0:<כמה שקלים הרוויח/ה [#1:n] בסך הכל באותו שבוע (שכר עבודה + בונוס)?>:<כמה שקלים היו במשכורת ללא הבונוס (רק עבור שעות העבודה)?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#BASE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#HOURS:add_(#BONUS)):mul_(#RATE):#R]>:<[NUM:value=(#BASE:sub_(#BONUS)):abs:#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_20):#R]>:<[NUM:value=(#BASE:add_15):#R]>]",
                "[IF:(#W)=0:<כפול את שכר השעה במספר השעות שעבד, ואז הוסף לתוצאה את הבונוס.>:<פשוט הכפל את שכר השעה במספר שעות העבודה בלבד.>]"
        });

        // 42
        newTemplates.add(new String[]{
                "[PLACE:place_type=HOME:*:#P1][NUM:min=4;max=8:*:#L][NUM:min=3;max=6:*:#W_VAL][NUM:value=(#L:mul_(#W_VAL)):*:#AREA][NUM:min=40;max=100;round=10:*:#PRICE][NUM:value=(#AREA:mul_(#PRICE)):*:#TOTAL_COST][NUM:min=0;max=1:*:#W]משפחה משפצת חדר ב[#P1:s]. אורך החדר מלבני הוא [#L] מטרים ורוחבו [#W_VAL] מטרים. הם רוצים לרצף את החדר. [IF:(#W)=0:<אם מחיר כל מ\"ר (מטר רבוע) של ריצוף הוא [#PRICE] שקלים, כמה יעלה לרצף את כל החדר?>:<אם עלות הריצוף הכוללת יצאה [#TOTAL_COST] שקלים, ומחיר הריצוף למ\"ר הוא [#PRICE] שקלים, מהו שטחו של החדר במ\"ר?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST):#R]>:<[NUM:value=(#AREA):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AREA):#R]>:<[NUM:value=(#TOTAL_COST):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#L:add_(#W_VAL)):mul_(#PRICE):#R]>:<[NUM:value=(#AREA:mul_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST:div_2):#R]>:<[NUM:value=(#AREA:add_5):#R]>]",
                "[IF:(#W)=0:<חשב קודם את שטח החדר (אורך כפול רוחב), ואת השטח שכפול במחיר למטר רבוע.>:<חלק את העלות הכוללת במחיר למטר רבוע אחד.>]"
        });
        // 1 (hard_logic_1)
        newTemplates.add(new String[]{
                "[PLACE:place_type=HOME|EDUCATION:*:#P1][HUMAN:*:#1][NUM:min=4;max=9:*:#WINS][NUM:min=10;max=15:*:#PTS_WIN][NUM:min=2;max=5:*:#LOSSES][NUM:min=3;max=8:*:#PTS_LOSS][NUM:value=(#WINS:mul_(#PTS_WIN)):*:#TOTAL_WIN][NUM:value=(#LOSSES:mul_(#PTS_LOSS)):*:#TOTAL_LOSS][NUM:min=0;max=1:*:#W][#1:n] [VERB:id=play:(present_+(#1:g)+_s):#V1] במשחק תחרותי ב[#P1:s]. על כל ניצחון מקבלים [#PTS_WIN] נקודות, ועל כל הפסד יורדות [#PTS_LOSS] נקודות. [IF:(#W)=0:<אם [#1:he_she] [VERB:id=win:(past_+(#1:g)+_s):#V2] ב-[#WINS] שלבים ו[VERB:id=lose_game:(past_+(#1:g)+_s):#V4] בשלב אחד, כמה נקודות יש [#1:to_him_her]?>:<אם [#1:he_she] [VERB:id=win:(past_+(#1:g)+_s):#V3] ב-[#WINS] שלבים ו[VERB:id=lose_game:(past_+(#1:g)+_s):#V5] ב-[#LOSSES] שלבים, כמה נקודות יש [#1:to_him_her]?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_WIN:sub_(#PTS_LOSS)):#R]>:<[NUM:value=(#TOTAL_WIN:sub_(#TOTAL_LOSS)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_WIN:add_(#PTS_LOSS)):#R]>:<[NUM:value=(#TOTAL_WIN:add_(#TOTAL_LOSS)):#R]>]",
                "[NUM:value=(#TOTAL_WIN):#R]",
                "[IF:(#W)=0:<[NUM:value=(#WINS:sub_(#PTS_LOSS)):#R]>:<[NUM:value=(#TOTAL_WIN:sub_(#LOSSES)):#R]>]",
                "[IF:(#W)=0:<חשב את נקודות הניצחון (כפל), וחסר מהן את הנקודות של הפסד אחד.>:<חשב את נקודות הניצחון, חשב את נקודות ההפסד (כפל נוסף), וחסר ביניהם.>]"
        });

        // 2 (hard_logic_2)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][PLACE:place_type=STORE:*:#P1][NUM:min=3;max=6:*:#A][NUM:min=15;max=25:*:#P1_PRICE][NUM:value=(#A:mul_(#P1_PRICE)):*:#COST_A][NUM:min=4;max=8:*:#B][NUM:min=12;max=20:*:#P2_PRICE][NUM:value=(#B:mul_(#P2_PRICE)):*:#COST_B][NUM:value=(#COST_A:add_(#COST_B)):*:#TOTAL_COST][NUM:value=(#TOTAL_COST:add_20):*:#BUDGET][NUM:min=0;max=1:*:#W]ל-[#1:n] היו [#BUDGET] שקלים. [#1:he_she] הלך/ה ל[#P1:s] וקנה/תה [#A] יחידות של מוצר א' במחיר של [#P1_PRICE] שקלים ליחידה, ועוד [#B] יחידות של מוצר ב' במחיר של [#P2_PRICE] שקלים ליחידה. [IF:(#W)=0:<כמה עודף נשאר ל-[#1:n] לאחר קניית כל המוצרים?>:<אם ידוע ש-[#1:n] קיבל/ה 20 שקלים עודף, ומוצר א' עלה [#P1_PRICE] ליחידה, כמה שילם/מה סך הכל על כל היחידות של מוצר ב' יחד?>]",
                "[IF:(#W)=0:<[NUM:value=20:#R]>:<[NUM:value=(#COST_B):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST):#R]>:<[NUM:value=(#P2_PRICE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BUDGET:sub_(#COST_A)):#R]>:<[NUM:value=(#TOTAL_COST):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BUDGET:sub_(#COST_B)):#R]>:<[NUM:value=(#COST_A):#R]>]",
                "[IF:(#W)=0:<חשב את העלות של מוצר א' (כפל), את העלות של מוצר ב' (כפל), חבר אותן, וחסר מהתקציב ההתחלתי.>:<חסר מהתקציב ההתחלתי את העודף ואת העלות הכוללת של יחידות מוצר א'.>]"
        });

        // 3 (hard_logic_3)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][NUM:min=20;max=30:*:#AVG1][NUM:value=(#AVG1:mul_4):*:#SUM1][NUM:min=31;max=45:*:#AVG2][NUM:value=(#AVG2:mul_5):*:#SUM2][NUM:value=(#SUM2:sub_(#SUM1)):*:#NEW_VAL][NUM:min=0;max=1:*:#W]ממוצע הציונים של [#1:n] ב-4 מבחנים היה [#AVG1]. [IF:(#W)=0:<במבחן החמישי [#1:he_she] קיבל/ה ציון של [#NEW_VAL]. מהו הממוצע החדש של [#1:n] בכל 5 המבחנים יחד?>:<לאחר המבחן החמישי, הממוצע של [#1:n] בכל 5 המבחנים עלה ל-[#AVG2]. איזה ציון הוא/היא קיבל/ה במבחן החמישי בלבד?>]",
                "[IF:(#W)=0:<[NUM:value=(#AVG2):#R]>:<[NUM:value=(#NEW_VAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AVG1:add_(#NEW_VAL)):div_2:#R]>:<[NUM:value=(#SUM2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM2):#R]>:<[NUM:value=(#AVG2:sub_(#AVG1)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#AVG2:add_5):#R]>:<[NUM:value=(#SUM1:add_(#AVG2)):#R]>]",
                "[IF:(#W)=0:<מצא את סכום הציונים של 4 המבחנים (ממוצע כפול 4), הוסף את הציון החמישי, וחלק הכל ב-5.>:<מצא את סכום הציונים החדש (ממוצע חדש כפול 5), וחסר ממנו את סכום הציונים הישן (ממוצע ישן כפול 4).>]"
        });

        // 4 (hard_logic_4)
        newTemplates.add(new String[]{
                "[NUM:min=200;max=500;round=100:*:#TOTAL][NUM:min=20;max=40;round=10:*:#P1][NUM:value=(#TOTAL:mul_(#P1)):div_100:*:#PART1][NUM:value=(#TOTAL:sub_(#PART1)):*:#REM1][NUM:value=(#REM1:div_2):*:#PART2][NUM:value=(#REM1:sub_(#PART2)):*:#FINAL][NUM:value=(#PART1:add_(#PART2)):*:#SOLD_TOTAL][NUM:min=0;max=1:*:#W]בחנות היו [#TOTAL] פריטים בתחילת היום. בבוקר נמכרו [#P1] אחוזים (%) מכל הפריטים. בערב, נמכרו בדיוק חצי (50%) מכל הפריטים **שנשארו** בחנות אחרי הבוקר. [IF:(#W)=0:<כמה פריטים נשארו בחנות בסוף היום כולו?>:<כמה פריטים נמכרו בסך הכל (בבוקר ובערב יחד) במהלך היום?>]",
                "[IF:(#W)=0:<[NUM:value=(#FINAL):#R]>:<[NUM:value=(#SOLD_TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#REM1):#R]>:<[NUM:value=(#PART1):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PART1):#R]>:<[NUM:value=(#TOTAL:sub_(#PART2)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:div_2):#R]>:<[NUM:value=(#TOTAL:sub_(#P1)):#R]>]",
                "[IF:(#W)=0:<חשב כמה נמכרו בבוקר (אחוז מהסך הכל), חסר כדי למצוא כמה נשארו, ואז חלק ב-2 כדי למצוא מה נשאר בסוף.>:<חשב את המכירות של הבוקר, מצא כמה נשאר, חלק ב-2 כדי למצוא את המכירות של הערב, וחבר את שתי המכירות.>]"
        });

        // 5 (hard_logic_5)
        newTemplates.add(new String[]{
                "[NUM:min=60;max=90;round=10:*:#SPEED1][NUM:min=2;max=4:*:#TIME1][NUM:value=(#SPEED1:mul_(#TIME1)):*:#DIST1][NUM:min=40;max=70;round=10:*:#SPEED2][NUM:min=1;max=3:*:#TIME2][NUM:value=(#SPEED2:mul_(#TIME2)):*:#DIST2][NUM:value=(#DIST1:add_(#DIST2)):*:#TOTAL_DIST][NUM:min=0;max=1:*:#W]נהג יצא למסע. בחלק הראשון של הדרך הוא נסע במהירות של [#SPEED1] קמ\"ש במשך [#TIME1] שעות. לאחר מכן נכנס לפקקים, ובחלק השני של הדרך נסע במהירות של [#SPEED2] קמ\"ש. [IF:(#W)=0:<אם החלק השני של הדרך לקח לו [#TIME2] שעות, מהו המרחק הכולל (בקילומטרים) שעבר מתחילת המסע ועד סופו?>:<אם ידוע שהמרחק הכולל שעבר במסע (בשני החלקים יחד) היה בדיוק [#TOTAL_DIST] קילומטרים, כמה שעות לקח לו לנסוע את החלק השני של הדרך?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_DIST):#R]>:<[NUM:value=(#TIME2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#DIST1):#R]>:<[NUM:value=(#TOTAL_DIST:div_(#SPEED2)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SPEED1:add_(#SPEED2)):mul_(#TIME1:add_(#TIME2)):#R]>:<[NUM:value=(#DIST2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_DIST:sub_20):#R]>:<[NUM:value=(#TIME1:add_(#TIME2)):#R]>]",
                "[IF:(#W)=0:<חשב את המרחק של החלק הראשון (מהירות כפול זמן), את המרחק של החלק השני, וחבר אותם.>:<חשב את המרחק של החלק הראשון, חסר אותו מהמרחק הכולל, ואת התוצאה חלק במהירות של החלק השני.>]"
        });

        // 6 (hard_logic_6)
        newTemplates.add(new String[]{
                "[NUM:min=10;max=20:*:#BOTH][NUM:min=15;max=30:*:#ONLY_A][NUM:min=10;max=25:*:#ONLY_B][NUM:value=(#ONLY_A:add_(#BOTH)):*:#A][NUM:value=(#ONLY_B:add_(#BOTH)):*:#B][NUM:value=(#ONLY_A:add_(#ONLY_B)):add_(#BOTH):*:#TOTAL][NUM:min=0;max=1:*:#W]בכיתה יש [#TOTAL] תלמידים. כל תלמיד חבר לפחות בחוג אחד (מתמטיקה או אנגלית). ידוע שיש [#A] תלמידים בחוג מתמטיקה, ו-[#B] תלמידים בחוג אנגלית. [IF:(#W)=0:<כמה תלמידים חברים ב**שני** החוגים יחד (גם מתמטיקה וגם אנגלית)?>:<כמה תלמידים חברים **רק** בחוג אנגלית (ולא במתמטיקה)?>]",
                "[IF:(#W)=0:<[NUM:value=(#BOTH):#R]>:<[NUM:value=(#ONLY_B):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#ONLY_A:add_(#ONLY_B)):#R]>:<[NUM:value=(#B):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_(#A)):#R]>:<[NUM:value=(#TOTAL:sub_(#BOTH)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#A:add_(#B)):#R]>:<[NUM:value=(#BOTH):#R]>]",
                "[IF:(#W)=0:<חבר את מספר תלמידי מתמטיקה ואנגלית. ההפרש בין הסכום הזה לסך כל התלמידים בכיתה הוא מספר התלמידים שבשני החוגים.>:<חסר מסך כל התלמידים בכיתה את כל התלמידים שנמצאים בחוג מתמטיקה (כי מי שלא במתמטיקה חייב להיות רק באנגלית).>]"
        });

        // 7 (hard_logic_7)
        newTemplates.add(new String[]{
                "[NUM:min=5;max=15:*:#WIDTH][NUM:value=(#WIDTH:mul_2):*:#LENGTH][NUM:value=(#WIDTH:mul_6):*:#PERIM][NUM:value=(#WIDTH:mul_(#LENGTH)):*:#AREA][NUM:min=0;max=1:*:#W]נתון מגרש מלבני. אורך המגרש גדול בדיוק פי 2 מהרוחב שלו. [IF:(#W)=0:<אם ידוע שהיקף המגרש הוא [#PERIM] מטרים, מהו שטח המגרש במטרים רבועים (מ\"ר)?>:<אם ידוע ששטח המגרש הוא [#AREA] מ\"ר ורוחבו הוא [#WIDTH] מטרים, מהו היקף המגרש במטרים?>]",
                "[IF:(#W)=0:<[NUM:value=(#AREA):#R]>:<[NUM:value=(#PERIM):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PERIM:div_2):mul_(#PERIM:div_2):#R]>:<[NUM:value=(#LENGTH:add_(#WIDTH)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PERIM):#R]>:<[NUM:value=(#AREA:div_(#WIDTH)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#LENGTH:mul_4):#R]>:<[NUM:value=(#AREA:add_(#WIDTH)):#R]>]",
                "[IF:(#W)=0:<היקף מלבן שווה ל-6 פעמים הרוחב (כי האורך הוא פעמיים הרוחב). מצא את הרוחב, מצא את האורך, וכפול ביניהם לשטח.>:<מצא קודם את האורך (כפול את הרוחב ב-2 או חלוק שטח ברוחב), ואז חשב את סכום כל הצלעות להיקף.>]"
        });

        // 8 (hard_logic_8)
        newTemplates.add(new String[]{
                "[NUM:min=5;max=12:*:#M][NUM:value=(#M:mul_3):*:#A][NUM:value=(#M:mul_5):*:#B][NUM:value=(#A:add_(#B)):*:#SUM][NUM:value=(#B:sub_(#A)):*:#DIFF][NUM:min=0;max=1:*:#W]בקופסה יש כדורים אדומים וכחולים. היחס בין מספר הכדורים האדומים למספר הכדורים הכחולים הוא 3:5 (על כל 3 אדומים יש 5 כחולים). [IF:(#W)=0:<אם ידוע שיש בקופסה סך הכל [#SUM] כדורים, בכמה גדול מספר הכדורים הכחולים ממספר הכדורים האדומים?>:<אם ידוע שיש בקופסה [#DIFF] כדורים כחולים יותר מאשר אדומים, כמה כדורים יש בסך הכל בקופסה (אדומים וכחולים יחד)?>]",
                "[IF:(#W)=0:<[NUM:value=(#DIFF):#R]>:<[NUM:value=(#SUM):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM:div_8):#R]>:<[NUM:value=(#DIFF:mul_5):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#B):#R]>:<[NUM:value=(#A):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#A):#R]>:<[NUM:value=(#DIFF:mul_3):#R]>]",
                "[IF:(#W)=0:<חבר את חלקי היחס (3+5) וחלק את הסכום הכולל בתוצאה כדי למצוא \"מנה\" אחת. ההפרש בין החלקים (2) כפול המנה ייתן את התשובה.>:<מצא כמה שווה \"מנה\" אחת על ידי חלוקת ההפרש להפרש חלקי היחס (5-3), ואז כפול את המנה בסכום כל החלקים (8).>]"
        });

        // 9 (hard_logic_9)
        newTemplates.add(new String[]{
                "[NUM:min=2;max=4:*:#NUM_BULK][NUM:min=2;max=4:*:#REMAIN][NUM:value=(#NUM_BULK:mul_6):add_(#REMAIN):*:#TARGET][NUM:min=40;max=60;round=10:*:#BULK_PRICE][NUM:min=10;max=15:*:#SINGLE_PRICE][NUM:value=(#NUM_BULK:mul_(#BULK_PRICE)):*:#COST_BULK][NUM:value=(#REMAIN:mul_(#SINGLE_PRICE)):*:#COST_SINGLE][NUM:value=(#COST_BULK:add_(#COST_SINGLE)):*:#TOTAL_COST][NUM:min=0;max=1:*:#W]חנות מוכרת מחברות: מארז של 6 מחברות עולה [#BULK_PRICE] שקלים, ומחברת בודדת עולה [#SINGLE_PRICE] שקלים. לקוח רוצה לקנות מחברות ולשלם כמה שפחות. [IF:(#W)=0:<אם הלקוח זקוק ל-[#TARGET] מחברות בדיוק, וקונה את הכמות המקסימלית של מארזים ואת השאר כבודדות, כמה ישלם סך הכל?>:<הלקוח קנה [#NUM_BULK] מארזים ועוד כמה מחברות בודדות. סך הכל הוא שילם [#TOTAL_COST] שקלים. כמה מחברות בודדות הוא קנה מחוץ למארזים?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST):#R]>:<[NUM:value=(#REMAIN):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TARGET:mul_(#SINGLE_PRICE)):#R]>:<[NUM:value=(#COST_SINGLE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#COST_BULK):#R]>:<[NUM:value=(#TARGET):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST:sub_10):#R]>:<[NUM:value=(#REMAIN:add_1):#R]>]",
                "[IF:(#W)=0:<בדוק כמה מארזים מלאים של 6 נכנסים בכמות המבוקשת, חשב את מחירם, ואז הוסף את מחיר השארית כפול מחיר מחברת בודדת.>:<חסר מהתשלום הכולל את המחיר של המארזים, ואת העודף שנשאר חלק במחיר של מחברת בודדת.>]"
        });

        // 10 (hard_logic_10)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][HUMAN:n=!(#1:n):*:#2][NUM:min=12;max=20:*:#FUT_B][NUM:value=(#FUT_B:mul_2):*:#FUT_A][NUM:min=5;max=9:*:#Y][NUM:value=(#FUT_B:sub_(#Y)):*:#B_AGE][NUM:value=(#FUT_A:sub_(#Y)):*:#A_AGE][NUM:value=(#A_AGE:add_(#B_AGE)):*:#SUM_NOW][NUM:min=0;max=1:*:#W]כיום, גילו של האח הגדול ([#1:n]) הוא [#A_AGE] שנים, וגילו של האח הקטן ([#2:n]) הוא [#B_AGE] שנים. [IF:(#W)=0:<בעוד כמה שנים מהיום, גילו של האח הגדול יהיה בדיוק פי 2 מגילו של האח הקטן?>:<בעוד [#Y] שנים מהיום, גילו של האח הגדול יהיה בדיוק פי 2 מגילו של האח הקטן (שיגיע לגיל [#FUT_B]). מהו סכום הגילים של שני האחים כיום?>]",
                "[IF:(#W)=0:<[NUM:value=(#Y):#R]>:<[NUM:value=(#SUM_NOW):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#FUT_B):#R]>:<[NUM:value=(#FUT_A:add_(#FUT_B)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#A_AGE:sub_(#B_AGE)):#R]>:<[NUM:value=(#SUM_NOW:add_(#Y)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#Y:add_2):#R]>:<[NUM:value=(#A_AGE):#R]>]",
                "[IF:(#W)=0:<מצא את ההפרש בין הגילים שלהם (הפרש שתמיד נשאר קבוע). בעתיד, הגיל של הקטן חייב להיות שווה להפרש הזה כדי שהגדול יהיה פי 2. חסר מזה את הגיל הנוכחי של הקטן.>:<חסר [#Y] שנים מגיל העתיד של הקטן כדי למצוא את גילו היום, וחסר [#Y] מגיל העתיד של הגדול כדי למצוא את גילו היום. חבר אותם.>]"
        });

        // 11 (hard_logic_11)
        newTemplates.add(new String[]{
                "[NUM:min=30;max=60;round=5:*:#COST_P][NUM:min=10;max=25;round=5:*:#PROFIT_P][NUM:value=(#COST_P:add_(#PROFIT_P)):*:#SELL_P][NUM:min=15;max=30:*:#QTY][NUM:value=(#QTY:mul_(#PROFIT_P)):*:#TOTAL_PROFIT][NUM:value=(#QTY:mul_(#SELL_P)):*:#TOTAL_REVENUE][NUM:value=(#QTY:mul_(#COST_P)):*:#TOTAL_COST][NUM:min=0;max=1:*:#W]בעל חנות מחשבים קנה [#QTY] פריטים זהים. [IF:(#W)=0:<הוא קנה כל פריט ב-[#COST_P] שקלים. הוא מכר את כל הפריטים ברווח נקי כולל של [#TOTAL_PROFIT] שקלים. בכמה שקלים הוא מכר פריט אחד (מהו מחיר המכירה ליחידה)?>:<הוא מוכר כל פריט ב-[#SELL_P] שקלים, ומרוויח עליו בדיוק [#PROFIT_P] שקלים נטו (מעבר לעלות הקנייה). אם סך כל ההכנסות שלו מהמכירות (הפדיון הכולל) היה [#TOTAL_REVENUE] שקלים, כמה עלה לו לקנות את כל הפריטים יחד בהתחלה (עלות הקנייה הכוללת)?>]",
                "[IF:(#W)=0:<[NUM:value=(#SELL_P):#R]>:<[NUM:value=(#TOTAL_COST):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PROFIT_P):#R]>:<[NUM:value=(#TOTAL_PROFIT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_PROFIT:div_(#QTY)):#R]>:<[NUM:value=(#TOTAL_REVENUE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#COST_P:add_(#TOTAL_PROFIT)):#R]>:<[NUM:value=(#TOTAL_COST:add_(#PROFIT_P)):#R]>]",
                "[IF:(#W)=0:<חלק את הרווח הכולל במספר הפריטים כדי למצוא את הרווח לפריט אחד, ואז הוסף אותו למחיר הקנייה המקורי של הפריט.>:<חסר את הרווח ליחידה ממחיר המכירה ליחידה כדי למצוא את עלות הקנייה, ואז כפול במספר הפריטים (פדיון כולל חלקי מחיר יחידה).>]"
        });

        // 12 (hard_logic_12)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][NUM:min=150;max=350;round=50:*:#TOTAL][NUM:value=(#TOTAL:div_5):mul_2:*:#READ1][NUM:min=30;max=60;round=10:*:#READ2][NUM:value=(#TOTAL:sub_(#READ1)):sub_(#READ2):*:#FINAL_LEFT][NUM:min=0;max=1:*:#W][#1:n] קורא/ת ספר. ביום הראשון [#1:he_she] קרא/ה בדיוק שתי חמישיות (2/5) מכל העמודים בספר. ביום השני [#1:he_she] קרא/ה עוד [#READ2] עמודים. [IF:(#W)=0:<אם ידוע שיש בספר [#TOTAL] עמודים סך הכל, כמה עמודים נשארו ל-[#1:n] לקרוא כדי לסיים את הספר?>:<אם ידוע שלאחר היום השני נותרו ל-[#1:n] בדיוק [#FINAL_LEFT] עמודים לסיום הספר, כמה עמודים יש בספר סך הכל?>]",
                "[IF:(#W)=0:<[NUM:value=(#FINAL_LEFT):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#READ1):#R]>:<[NUM:value=(#TOTAL:sub_(#READ1)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_(#READ1)):#R]>:<[NUM:value=(#READ1:add_(#READ2)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#FINAL_LEFT:add_10):#R]>:<[NUM:value=(#TOTAL:div_5):mul_3:#R]>]",
                "[IF:(#W)=0:<מצא כמה זה 2/5 מתוך סך העמודים, וחבר למספר העמודים שנקראו ביום השני. חסר את הסכום מסך העמודים בספר.>:<אם נחבר את העמודים מהיום השני והעמודים שנשארו, נקבל שלוש חמישיות (3/5) מהספר (כי 2/5 נקראו). חלק ב-3 וכפול ב-5 כדי למצוא את השלם.>]"
        });

        // 13 (hard_logic_13)
        newTemplates.add(new String[]{
                "[NUM:min=20;max=40;round=10:*:#NET_RATE][NUM:min=10;max=20:*:#TIME][NUM:value=(#NET_RATE:mul_(#TIME)):*:#CAP][NUM:min=10;max=25:*:#RATE_OUT][NUM:value=(#NET_RATE:add_(#RATE_OUT)):*:#RATE_IN][NUM:min=0;max=1:*:#W]לבאר מים מחוברים שני צינורות: צינור אחד מכניס מים פנימה בקצב של [#RATE_IN] ליטרים בדקה, וצינור שני מוציא (מרוקן) מים החוצה בקצב של [#RATE_OUT] ליטרים בדקה. שני הצינורות הופעלו יחד כשהבאר הייתה ריקה לחלוטין. [IF:(#W)=0:<אם נפח הבאר הוא [#CAP] ליטרים, אחרי כמה דקות הבאר תתמלא לחלוטין?>:<אם לקח לבאר בדיוק [#TIME] דקות להתמלא לחלוטין, מהו הנפח הכולל של הבאר בליטרים?>]",
                "[IF:(#W)=0:<[NUM:value=(#TIME):#R]>:<[NUM:value=(#CAP):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#CAP:div_(#RATE_IN)):#R]>:<[NUM:value=(#RATE_IN:mul_(#TIME)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#CAP:div_(#RATE_OUT)):#R]>:<[NUM:value=(#NET_RATE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TIME:add_5):#R]>:<[NUM:value=(#CAP:sub_50):#R]>]",
                "[IF:(#W)=0:<חשב את קצב המילוי הנטו (הכנסה פחות הוצאה) בדקה אחת, ואז חלק את נפח הבאר בקצב הזה.>:<חשב את קצב המילוי הנטו של הבאר בדקה אחת (כמה נכנס פחות כמה יוצא), והכפל במספר הדקות שלקח למלא אותה.>]"
        });

        // 14 (hard_logic_14)
        newTemplates.add(new String[]{
                "[NUM:min=15;max=35:*:#X][NUM:value=(#X:add_1):*:#X1][NUM:value=(#X:add_2):*:#X2][NUM:value=(#X:mul_3):add_3:*:#SUM][NUM:value=(#X:add_(#X2)):*:#EXTREMES][NUM:min=0;max=1:*:#W]הסכום של שלושה מספרים שלמים ועוקבים (שבאים אחד אחרי השני, כמו 4, 5, 6) הוא בדיוק [#SUM]. [IF:(#W)=0:<מהו המספר הגדול ביותר מבין השלושה?>:<מהו סכום שני המספרים הקיצוניים (המספר הקטן ביותר והמספר הגדול ביותר יחד) מתוך השלושה?>]",
                "[IF:(#W)=0:<[NUM:value=(#X2):#R]>:<[NUM:value=(#EXTREMES):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#X):#R]>:<[NUM:value=(#X2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#X1):#R]>:<[NUM:value=(#SUM:div_3):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM:div_3):#R]>:<[NUM:value=(#EXTREMES:add_1):#R]>]",
                "[IF:(#W)=0:<אם נחסר 3 מהסכום הכולל, נקבל שלוש פעמים את המספר הקטן ביותר. חלק ב-3, ומצא את הקטן, ואז הוסף 2 כדי למצוא את הגדול.>:<סכום שלושה מספרים עוקבים שווה לפי 3 מהמספר האמצעי. מצא את האמצעי, ואז סכום הקיצוניים הוא פשוט המספר האמצעי כפול 2.>]"
        });

        // 15 (hard_logic_15)
        newTemplates.add(new String[]{
                "[NUM:min=3;max=5:*:#M_B][NUM:value=(#M_B:mul_5):*:#BOYS][NUM:value=(#M_B):*:#GLASS_B][NUM:min=4;max=6:*:#M_G][NUM:value=(#M_G:mul_4):*:#GIRLS][NUM:value=(#M_G):*:#GLASS_G][NUM:value=(#BOYS:add_(#GIRLS)):*:#TOTAL][NUM:value=(#GLASS_B:add_(#GLASS_G)):*:#TOTAL_GLASS][NUM:value=(#TOTAL:sub_(#TOTAL_GLASS)):*:#NO_GLASS][NUM:min=0;max=1:*:#W]בשכבת כיתות ח' יש [#TOTAL] תלמידים. ידוע ש-[#BOYS] מהם הם בנים והשאר הן בנות. חמישית (1/5) מהבנים מרכיבים משקפיים, ורבע (1/4) מהבנות מרכיבות משקפיים. [IF:(#W)=0:<כמה תלמידים סך הכל (בנים ובנות יחד) מרכיבים משקפיים בשכבה?>:<כמה תלמידים (בנים ובנות יחד) בשכבה **אינם** מרכיבים משקפיים כלל?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_GLASS):#R]>:<[NUM:value=(#NO_GLASS):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#NO_GLASS):#R]>:<[NUM:value=(#TOTAL_GLASS):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#GLASS_B):#R]>:<[NUM:value=(#BOYS:sub_(#GLASS_B)):add_(#GIRLS):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#GLASS_G):#R]>:<[NUM:value=(#NO_GLASS:add_2):#R]>]",
                "[IF:(#W)=0:<מצא כמה בנות יש בשכבה (חיסור הבנים מהסך הכל). חלק את הבנים ב-5 ואת הבנות ב-4, וחבר את התוצאות.>:<חשב קודם כמה מרכיבים משקפיים (חמישית מהבנים ועוד רבע מהבנות), וחסר את התוצאה מסך כל התלמידים בשכבה.>]"
        });

        // 16 (hard_logic_16)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][HUMAN:n=!(#1:n):*:#2][HUMAN:n=!(#1:n);n=!(#2:n):*:#3][ITEM:*:#I1][NUM:min=4;max=8:*:#RA][NUM:min=5;max=9:*:#RB][NUM:min=3;max=7:*:#RC][NUM:value=(#RA:add_(#RB)):add_(#RC):*:#SUM_R][NUM:min=3;max=6:*:#H][NUM:value=(#SUM_R:mul_(#H)):*:#TOTAL][NUM:min=0;max=1:*:#W][#1:n], [#2:n] ו-[#3:n] אורזים [#I1:p]. ההספק של כל אחד בשעה אחת הוא: [#1:n] אורז/ת [#RA] פריטים, [#2:n] אורז/ת [#RB] פריטים, ו-[#3:n] אורז/ת [#RC] פריטים. [IF:(#W)=0:<אם שלושתם יעבדו יחד במשך [#H] שעות ברצף, כמה [#I1:p] הם יארזו סך הכל?>:<אם שלושתם עבדו יחד וארזו סך הכל [#TOTAL] [#I1:p], כמה שעות הם עבדו?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#H):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#RA:add_(#RB)):mul_(#H):#R]>:<[NUM:value=(#TOTAL:div_(#RA)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:sub_(#H)):#R]>:<[NUM:value=(#H:add_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SUM_R:add_(#H)):#R]>:<[NUM:value=(#TOTAL:div_(#RB)):#R]>]",
                "[IF:(#W)=0:<חבר את ההספק של שלושתם יחד כדי למצוא כמה הם אורזים בשעה אחת, ואז כפול במספר השעות.>:<מצא את ההספק המשותף של שלושתם בשעה אחת (חיבור), ואז חלק את הכמות הכוללת בהספק הזה.>]"
        });

        // 17 (hard_logic_17)
        newTemplates.add(new String[]{
                "[ITEM:*:#I1][NUM:min=2;max=5:*:#HUNDREDS][NUM:value=(#HUNDREDS:mul_100):*:#TOTAL][NUM:min=20;max=30;round=5:*:#P1][NUM:min=10;max=15:*:#P2][NUM:value=(#TOTAL:mul_(#P1)):div_100:*:#S1][NUM:value=(#TOTAL:mul_(#P2)):div_100:*:#S2][NUM:value=(#S1:add_(#S2)):*:#SOLD][NUM:value=(#TOTAL:sub_(#SOLD)):*:#LEFT][NUM:min=0;max=1:*:#W]בחנות היו [#TOTAL] [#I1:p]. ביום הראשון נמכרו [#P1]% מכל הפריטים שהיו בהתחלה. ביום השני נמכרו עוד [#P2]% מסך הפריטים **ההתחלתי**. [IF:(#W)=0:<כמה [#I1:p] נמכרו בסך הכל בשני הימים יחד?>:<כמה [#I1:p] נשארו בחנות לאחר שני ימי המכירות?>]",
                "[IF:(#W)=0:<[NUM:value=(#SOLD):#R]>:<[NUM:value=(#LEFT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#S1):#R]>:<[NUM:value=(#SOLD):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#LEFT):#R]>:<[NUM:value=(#TOTAL:sub_(#S1)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#SOLD:add_10):#R]>:<[NUM:value=(#LEFT:sub_10):#R]>]",
                "[IF:(#W)=0:<חבר את האחוזים של שני הימים (אחוז המכירה הכולל), וחשב את האחוז הזה מתוך הכמות ההתחלתית.>:<חשב כמה פריטים נמכרו (בעזרת חיבור האחוזים), וחסר את התוצאה מהכמות ההתחלתית הכוללת.>]"
        });

        // 18 (hard_logic_18)
        newTemplates.add(new String[]{
                "[NUM:min=80;max=110;round=10:*:#V2][NUM:min=50;max=70;round=10:*:#V1][NUM:value=(#V2:sub_(#V1)):*:#DIFF_V][NUM:min=2;max=5:*:#H][NUM:value=(#DIFF_V:mul_(#H)):*:#DIST][NUM:min=0;max=1:*:#W]שתי מכוניות יצאו מאותה נקודה ונסעו בדיוק באותו כיוון. המכונית הראשונה נסעה במהירות קבועה של [#V1] קמ\"ש, והשנייה נסעה במהירות קבועה של [#V2] קמ\"ש. [IF:(#W)=0:<מה יהיה המרחק בקילומטרים בין המכוניות לאחר [#H] שעות נסיעה?>:<לאחר כמה שעות נסיעה המרחק ביניהן יהיה בדיוק [#DIST] קילומטרים?>]",
                "[IF:(#W)=0:<[NUM:value=(#DIST):#R]>:<[NUM:value=(#H):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#V2:mul_(#H)):#R]>:<[NUM:value=(#DIST:div_(#V2)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#V1:mul_(#H)):#R]>:<[NUM:value=(#H:add_2):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#V1:add_(#V2)):mul_(#H):#R]>:<[NUM:value=(#DIST:div_(#V1)):#R]>]",
                "[IF:(#W)=0:<מצא את הפרש המהירויות ביניהן (בכמה ק\"מ השנייה מתרחקת מהראשונה בכל שעה), ואז כפול במספר השעות.>:<מצא את הפרש המהירויות ביניהן, וחלק את המרחק הנתון בהפרש הזה.>]"
        });

        // 19 (hard_logic_19)
        newTemplates.add(new String[]{
                "[NUM:min=8;max=15:*:#L][NUM:min=6;max=10:*:#W_VAL][NUM:value=(#L:mul_(#W_VAL)):*:#A_ROOM][NUM:value=(#L:sub_2):*:#C_L][NUM:value=(#W_VAL:sub_2):*:#C_W][NUM:value=(#C_L:mul_(#C_W)):*:#A_CARPET][NUM:value=(#A_ROOM:sub_(#A_CARPET)):*:#UNCOVERED][NUM:min=0;max=1:*:#W]אורך רצפת חדר מלבני הוא [#L] מטרים ורוחבה [#W_VAL] מטרים. הניחו במרכז החדר שטיח מלבני, כך שנשאר רווח (שוליים) של 1 מטר בדיוק בין כל צלע של השטיח לבין קירות החדר (מכל 4 הכיוונים). [IF:(#W)=0:<מהו שטח השטיח במ\"ר?>:<מהו השטח של רצפת החדר שנותר גלוי (ללא שטיח) במ\"ר?>]",
                "[IF:(#W)=0:<[NUM:value=(#A_CARPET):#R]>:<[NUM:value=(#UNCOVERED):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#A_ROOM):#R]>:<[NUM:value=(#A_CARPET):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#L:sub_1):mul_(#W_VAL:sub_1):#R]>:<[NUM:value=(#A_ROOM:sub_(#L:sub_1):mul_(#W_VAL:sub_1)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#C_L:add_(#C_W)):mul_2:#R]>:<[NUM:value=(#A_ROOM:sub_2):#R]>]",
                "[IF:(#W)=0:<כדי למצוא את אורך השטיח חסר 2 מטרים (1 מכל צד) מאורך החדר. עשה אותו דבר לרוחב, ואז חשב שטח (אורך כפול רוחב).>:<חשב את השטח הכולל של החדר, חשב את שטח השטיח (זכור להוריד 2 מטרים מהאורך ו-2 מהרוחב), וחסר את שטח השטיח משטח החדר.>]"
        });

        // 20 (hard_logic_20)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][NUM:min=10;max=30:*:#K][NUM:value=(#K:mul_12):*:#TOTAL][NUM:value=(#TOTAL:div_3):*:#FOOD][NUM:value=(#TOTAL:sub_(#FOOD)):*:#REM1][NUM:value=(#REM1:div_4):*:#CLOTHES][NUM:value=(#REM1:sub_(#CLOTHES)):*:#LEFT][NUM:min=0;max=1:*:#W]ל-[#1:n] היו [#TOTAL] שקלים. [#1:he_she] הוציא/ה בדיוק שליש (1/3) מהסכום הכולל על אוכל, ולאחר מכן הוציא/ה רבע (1/4) **ממה שנשאר** על קניית בגדים. [IF:(#W)=0:<כמה שקלים נשארו ל-[#1:n] בסוף שתי הקניות?>:<כמה שקלים הוציא/ה [#1:n] על קניית בגדים בלבד?>]",
                "[IF:(#W)=0:<[NUM:value=(#LEFT):#R]>:<[NUM:value=(#CLOTHES):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#CLOTHES):#R]>:<[NUM:value=(#LEFT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:div_4):mul_3:#R]>:<[NUM:value=(#TOTAL:div_4):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#REM1):#R]>:<[NUM:value=(#FOOD):#R]>]",
                "[IF:(#W)=0:<מצא כמה הוציא/ה על אוכל (חלק ב-3). חסר מהסכום כדי לדעת כמה נשאר. מצא רבע ממה שנשאר, וחסר שוב כדי לגלות את העודף הסופי.>:<מצא את הסכום שהוצא על אוכל, חסר אותו מהסכום הכולל כדי למצוא את השארית, ואת השארית הזו חלק ב-4.>]"
        });

        // 21 (hard_logic_21)
        newTemplates.add(new String[]{
                "[NUM:min=25;max=50:*:#S][NUM:min=4;max=8:*:#Y][NUM:value=(#Y:mul_3):*:#DIFF_YEARS][NUM:value=(#S:add_(#DIFF_YEARS)):*:#FUT][NUM:value=(#S:sub_(#DIFF_YEARS)):*:#PAST][NUM:min=0;max=1:*:#W]סכום הגילים של שלושה אחים כיום הוא בדיוק [#S] שנים. [IF:(#W)=0:<מה יהיה סכום הגילים של שלושתם יחד בעוד [#Y] שנים מהיום?>:<מה היה סכום הגילים של שלושתם יחד לפני [#Y] שנים (בהנחה שכולם כבר נולדו אז)?>]",
                "[IF:(#W)=0:<[NUM:value=(#FUT):#R]>:<[NUM:value=(#PAST):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#S:add_(#Y)):#R]>:<[NUM:value=(#S:sub_(#Y)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#S:add_(#Y:mul_2)):#R]>:<[NUM:value=(#S:sub_(#Y:mul_2)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PAST):#R]>:<[NUM:value=(#FUT):#R]>]",
                "[IF:(#W)=0:<זכור שכל אחד משלושת האחים גדל ב-[#Y] שנים. הוסף לסכום הנוכחי 3 פעמים את [#Y].>:<זכור שלפני [#Y] שנים, כל אחד משלושת האחים היה צעיר ב-[#Y] שנים. חסר מהסכום הנוכחי 3 פעמים את [#Y].>]"
        });

        // 22 (hard_logic_22)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][NUM:min=10;max=25:*:#X][NUM:min=5;max=12:*:#D][NUM:value=(#D:mul_3):*:#D3][NUM:value=(#X:add_(#D3)):*:#DAY4][NUM:value=(#X:mul_4):add_(#D:mul_6):*:#TOTAL][NUM:min=0;max=1:*:#W][#1:n] חוסך/ת כסף. ביום הראשון [#1:he_she] שמר/ה [#X] שקלים. בכל יום נוסף, [#1:he_she] מוסיף/פה לסכום השמירה היומי עוד [#D] שקלים יותר ממה ששמר/ה ביום הקודם (למשל, ביום השני שמר/ה [#X]+[#D] שקלים). [IF:(#W)=0:<כמה שקלים הוא/היא שמר/ה בקופה **רק ביום הרביעי** בלבד?>:<כמה שקלים הוא/היא שמר/ה בסך הכל במהלך כל 4 הימים הראשונים יחד?>]",
                "[IF:(#W)=0:<[NUM:value=(#DAY4):#R]>:<[NUM:value=(#TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#X:add_(#D:mul_4)):#R]>:<[NUM:value=(#DAY4):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#X:add_(#D:mul_2)):#R]>:<[NUM:value=(#X:mul_4):add_(#D:mul_4):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL):#R]>:<[NUM:value=(#X:add_(#D:mul_6)):#R]>]",
                "[IF:(#W)=0:<חשב שלב אחרי שלב: יום 1 הוא [#X], יום 2 הוא התוספת על יום 1, יום 3 הוא התוספת על יום 2, וכו'.>:<חשב כמה שקלים נשמרו בכל אחד מ-4 הימים בנפרד, ואז חבר את כל ארבעת הסכומים יחד.>]"
        });

        // 23 (hard_logic_23)
        newTemplates.add(new String[]{
                "[ITEM:*:#I1][NUM:min=12;max=25:*:#K][NUM:value=(#K:mul_7):*:#TOTAL][NUM:value=(#K:mul_2):*:#RED][NUM:value=(#K:mul_5):*:#BLUE][NUM:value=(#BLUE:sub_(#RED)):*:#DIFF][NUM:min=0;max=1:*:#W]בקופסה גדולה יש [#TOTAL] [#I1:p]. ידוע שחלקם אדומים וחלקם כחולים, והיחס בין מספר ה-[#I1:p] האדומים לכחולים הוא 2:5 (על כל 2 אדומים יש 5 כחולים). [IF:(#W)=0:<כמה [#I1:p] כחולים יש בקופסה?>:<בכמה גדול מספר ה-[#I1:p] הכחולים ממספר ה-[#I1:p] האדומים בקופסה?>]",
                "[IF:(#W)=0:<[NUM:value=(#BLUE):#R]>:<[NUM:value=(#DIFF):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#RED):#R]>:<[NUM:value=(#BLUE):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:div_5):#R]>:<[NUM:value=(#TOTAL:sub_(#BLUE)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#DIFF):#R]>:<[NUM:value=(#RED):#R]>]",
                "[IF:(#W)=0:<חבר את חלקי היחס (2+5=7). חלק את הסכום הכולל ב-7 כדי למצוא שווי של \"חלק\" אחד, ואז כפול ב-5.>:<מצא מהו שווי של \"חלק\" אחד (חלוקת הסך הכל ב-7). ההפרש ביחס הוא 3 (5-2), לכן כפול את החלק ב-3.>]"
        });

        // 24 (hard_logic_24)
        newTemplates.add(new String[]{
                "[HUMAN:*:#1][NUM:min=12;max=20:*:#P][NUM:min=15;max=30:*:#K][NUM:value=(#P:mul_(#K)):*:#TOTAL][NUM:value=(#K:mul_10):*:#TIME_TOTAL][NUM:value=(#P:mul_12):*:#P_120][NUM:min=0;max=1:*:#W][#1:n] קורא/ת בדיוק [#P] עמודים בכל 10 דקות. [IF:(#W)=0:<אם בספר יש [#TOTAL] עמודים, כמה דקות ייקח לו/לה לקרוא את כולו ברצף?>:<אם [#1:he_she] יקרא/תקרא ברצף במשך שעתיים (120 דקות), כמה עמודים [#1:he_she] יספיק/תספיק לקרוא?>]",
                "[IF:(#W)=0:<[NUM:value=(#TIME_TOTAL):#R]>:<[NUM:value=(#P_120):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL:div_(#P)):#R]>:<[NUM:value=(#P:mul_10):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TIME_TOTAL:mul_10):#R]>:<[NUM:value=(#TIME_TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#P_120):#R]>:<[NUM:value=(#P:mul_2):#R]>]",
                "[IF:(#W)=0:<חלק את מספר העמודים הכולל בכמות העמודים שנקראת ב-10 דקות, ואת התוצאה כפול ב-10.>:<חשב כמה פעמים נכנסות \"10 דקות\" בתוך 120 דקות (12 פעמים), ואז כפול את התוצאה במספר העמודים.>]"
        });

        // 25 (hard_logic_25)
        newTemplates.add(new String[]{
                "[NUM:min=15;max=30:*:#HALF][NUM:value=(#HALF:mul_2):*:#TOTAL_ITEMS][NUM:min=20;max=40:*:#COST][NUM:value=(#TOTAL_ITEMS:mul_(#COST)):*:#TOTAL_COST][NUM:value=(#COST:add_10):*:#S1][NUM:value=(#COST:add_20):*:#S2][NUM:value=(#HALF:mul_(#S1)):*:#REV1][NUM:value=(#HALF:mul_(#S2)):*:#REV2][NUM:value=(#REV1:add_(#REV2)):*:#REV_TOTAL][NUM:value=(#REV_TOTAL:sub_(#TOTAL_COST)):*:#PROFIT][NUM:min=0;max=1:*:#W]סוחר קנה [#TOTAL_ITEMS] מוצרים זהים. עלות כל מוצר עבורו הייתה [#COST] שקלים. הוא מכר חצי מהמוצרים במחיר של [#S1] שקלים ליחידה, ואת החצי השני במחיר של [#S2] שקלים ליחידה. [IF:(#W)=0:<מה היו ההכנסות הכוללות שלו מהמכירה בלבד (הפדיון הכולל)?>:<מה היה הרווח הנקי שלו בסך הכל (הכנסות פחות עלויות קנייה)?>]",
                "[IF:(#W)=0:<[NUM:value=(#REV_TOTAL):#R]>:<[NUM:value=(#PROFIT):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#PROFIT):#R]>:<[NUM:value=(#REV_TOTAL):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST):#R]>:<[NUM:value=(#REV_TOTAL:sub_(#COST)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_ITEMS:mul_(#S1:add_(#S2))):#R]>:<[NUM:value=(#PROFIT:add_50):#R]>]",
                "[IF:(#W)=0:<מצא כמה זה חצי מהמוצרים. חשב את ההכנסות מהחצי הראשון, חשב את ההכנסות מהחצי השני, וחבר אותן יחד.>:<חשב את סך כל ההכנסות משני החצאים, ואז חסר מהסכום הזה את העלות הכוללת של קניית כל המוצרים (כמות כפול מחיר קנייה).>]"
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

