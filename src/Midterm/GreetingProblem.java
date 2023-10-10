package Midterm;

import java.util.List;

public class GreetingProblem {

    private int id;
    private String content;

    public Greeting getHighestGreeting(List<Greeting> list) {
        if(list.isEmpty()) {
            return null;
        }

        Greeting highestGreeting = list.get(0);

        for(Greeting g: list) {
            if(g.getId() > highestGreeting.getId()) {
                highestGreeting = g;
            }
        }
        return highestGreeting;
    }
    public int getId() {
        return id;
    }
    public String getContent() {
        return content;
    }
}
