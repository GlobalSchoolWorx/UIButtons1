package com.edu.worx.global;

public class HomeworkSet {

    public long UIN;
    public String CONTENT;
    public String DUEDATE;
    public String ISSUEDATE;
    public String TYPE;

    public HomeworkSet () {}

    @Override
    public String toString() {
        return "HomeworkSet{" +
                "UIN=" + UIN +
                ", Content='" + CONTENT + '\'' +
                ", Due Date='" + DUEDATE + '\'' +
                ", Issue Date='" + ISSUEDATE + '\'' +
                ", Type='" + TYPE + '\'' +
                '}';
    }
};