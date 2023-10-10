import Midterm.Greeting;
import Midterm.GreetingProblem;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GreetingTest {

    @Test
    public void testGetHighestGreetingWithEmptyList() {
        List<Greeting> emptyList = new ArrayList<>();
        GreetingProblem greetingUtils = new GreetingProblem();

        Greeting result = greetingUtils.getHighestGreeting(emptyList);

        Assert.assertNull(result);
    }

    @Test
    public void testGetHighestGreetingWithSingleGreeting() {
        List<Greeting> greetings = new ArrayList<>();
        Greeting greeting = new Greeting();
        greeting.setId(1);
        greetings.add(greeting);
        GreetingProblem greetingUtils = new GreetingProblem();

        Greeting result = greetingUtils.getHighestGreeting(greetings);

        Assert.assertEquals(1, result.getId());
    }

    @Test
    public void testGetHighestGreetingWithMultipleGreetings() {
        List<Greeting> greetings = new ArrayList<>();
        Greeting greeting1 = new Greeting();
        greeting1.setId(1);
        Greeting greeting2 = new Greeting();
        greeting2.setId(3);
        Greeting greeting3 = new Greeting();
        greeting3.setId(2);
        greetings.add(greeting1);
        greetings.add(greeting2);
        greetings.add(greeting3);
        GreetingProblem greetingUtils = new GreetingProblem();

        Greeting result = greetingUtils.getHighestGreeting(greetings);

        Assert.assertEquals(3, result.getId());
    }
}
