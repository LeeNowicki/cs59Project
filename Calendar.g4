grammar Calendar;

start : line (line)*;

line: keyword '\n';

keyword: action 
        | event ;

action: 'Invite' NAME ('to')? NAME ('on')? date (file)?
        | 'Repeat' NAME frequency 
        | 'Extend' NAME date duration
        | 'Cancel' NAME (date)?
        | 'Reminder' NAME (date| date TIME| TIME) (file)?
        |
        ;

event: TYPE NAME time (date)? ;

// When parsing the repeats. We will have two JSONs
// "Repeat": [Annually, Monthly]
// "RepeatDay": [0, 3, 4]
frequency: 'Every' weekday ('and' weekday)*
        |'Biweekly' (weekday)? ('and' weekday)*
        |'Annually'
        |'Weekly' (weekday)? ('and' weekday)*
        |'Monthly'
        //etc
        ;
weekday: 'Mon' | 'Monday'
    | 'Tues' | 'Tuesday'
    |'Wed' | 'Wednesday'
    |'Thurs' | 'Thursday'
    |'Fri' | 'Friday'
    |'Sat' | 'Saturday'
    |'Sun' | 'Sunday';

file: 'stdout'; //add others

duration: (num ('hours'|'min'|'sec'))+;

time: TIME
    | TIME 'to' TIME
    |TIME '-' TIME;


date: ('Jan'|'Mar'|'May'|'July'|'Aug'|'Oct'|'Dec'|'January'|'March'|'May'|'July'|'August'|'October'|'December') (THIRTYONE|TWENTYEIGHT|TWENTYNINE|THIRTY) (num)?
    | ('Apr'|'Jun'|'Sep'|'Nov'|'April'|'June'|'Sepember'|'November') (THIRTY|TWENTYEIGHT|TWENTYNINE) (num)?
    | ('Feb'|'February') TWENTYEIGHT (num)?
//    | ('1/'|'3/'|'5/'|'7/'|'8/'|'10/'|'12/') (THIRTYONE|TWENTYEIGHT|TWENTYNINE|THIRTY) NUMERICYEAR
//    | ('4/'|'6/'|'9/'|'11/') (THIRTY|TWENTYEIGHT|TWENTYNINE) NUMERICYEAR
//    | '2/' TWENTYEIGHT NUMERICYEAR;
    | NUMERICDATE;

num: INT | TWENTYNINE | TWENTYEIGHT | THIRTY | THIRTYONE;

NAME :'"'[A-Za-z0-9 ]+'"' ;//Matches double quoted string

TYPE: [A-Za-z]([A-Za-z0-9])*;

TIME: ([0-1][0-9]|'2'[0-3])(':')([0-5][0-9]);
//|((([1-9](':')[0-5][0-9])|('1'[0-2](':')[0-5][0-9]))('am'|'AM'|'pm'|'PM')); time in am/pm format is a stretch goal

TWENTYEIGHT: ([1-9]|[1-2][0-8]);

TWENTYNINE: ([1-9]|[1-2][0-9]);

THIRTY: ([1-9]|[1-2][0-9]|'30');
        
THIRTYONE: ([1-9]|[1-2][0-9]|'3'[0-1]);

NUMERICDATE: ((('1/'|'3/'|'5/'|'7/'|'8/'|'10/'|'12/')[1-9]|[1-2][0-9]|'3'[0-1])|(('4/'|'6/'|'9/'|'11/')[1-9]|[1-2][0-9]|'30')|(('2/')[1-9]|[1-2][0-8]))('/'[0-9][0-9][0-9][0-9]);

INT: [0-9]([0-9])*;

WS: [ \t\r] -> skip;