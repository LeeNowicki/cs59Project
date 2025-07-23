grammar Calendar;

start : line (line)* EOF;

line: keyword '\n'; 

keyword: action 
        | event ;

action: 'Invite' NAME ('to')? event (file)?
        | 'Repeat' NAME frequency 
        | 'Extend' NAME date duration
        | 'Cancel' NAME (date)?
        | 'Reminder' NAME (date| date time| time) (file)?
        |
        ;

event: ID (NAME)? time (date)? ;

frequency: 'Every' weekday ('and' weekday)*
        |'Biweekly' (weekday)? ('and' weekday)*
        |'Annually'
        |'Weekly' (weekday)? ('and' weekday)*
        |'Monthly'
        //etc
        ;
weekday: 'Mon'
    | 'Tues'
    |'Wed' | 'Wednesday'
    |'Thurs'
    |'Fri'
    |'Sat'
    |'Sun';

file: 'stdout'; //add others

duration: (INT ('hours'|'min'|'sec'))+;

time: TIME
    | TIME 'to' TIME
    |TIME '-' TIME;


date: ('Jan'|'Mar'|'May'|'July'|'Aug'|'Oct'|'Dec') THIRTYONE
    | ('Apr'|'Jun'|'Sep'|'Nov') THIRTY
    | 'Feb' TWENTYEIGHT
    |NUMERICDATE;



NAME :'"'.*'"' ;//Matches double quoted string

ID : [A-Za-z]([A-Za-z0-9])*;

INT: [0-9]([0-9])*;
        
THIRTYONE: ([1-9]|[1-2][0-9]|'3'[0-1]);

THIRTY: ([1-9]|[1-2][0-9]|'30');

TWENTYNINE: ([1-9]|[1-2][0-9]);

TWENTYEIGHT: ([1-9]|[1-2][0-8]);

TIME: ([0-1][0-9]|'2'[0-3])(':')?([0-5][0-9]);

NUMERICDATE: ; //We'll do that later