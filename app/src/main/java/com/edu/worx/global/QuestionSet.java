package com.edu.worx.global;

public class QuestionSet {

    public long UIN;
    public String Answer;
    public String Description;
    public String Keywords;
    public String Level;
    public String OptionA;
    public String OptionB;
    public String OptionC;
    public String OptionD;
    public String Question;
    public String Std;
    public String SubTopic;
    public String SubTopicID;
    public String TimeLimit;
    public String Topic;
    public int    Freq;

    public QuestionSet () {

    }
    public QuestionSet (java.lang.String inQuestion) {
        this.Question = inQuestion;
    }

    public QuestionSet (long inUIN, String inAnswer, String inDescription, String inKeywords, String inLevel,
                             String inOptionA, String inOptionB, String inOptionC, String inOptionD,
                             String inQuestion, String inStd, String inSubTopic, String inTimeLimit,
                             String inTopic) {

        Answer = inAnswer;
        Description = inDescription;
        Keywords = inKeywords;
        Level = inLevel;
        OptionA = inOptionA;
        OptionB = inOptionB;
        OptionC = inOptionC;
        OptionD = inOptionD;
        Question = inQuestion;
        Std = inStd;
        SubTopic = inSubTopic;
        TimeLimit = inTimeLimit;
        Topic = inTopic;
        UIN = inUIN;
    }

};