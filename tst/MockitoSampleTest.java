import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

public class MockitoSampleTest {

    @Test
    public void mockExampleTest() {
        ArrayList<String> names = Mockito.mock(ArrayList.class);
        Mockito.when(names.size()).thenReturn(1000000);
        Mockito.when(names.get(0)).thenReturn("Mr. Paul");

        Assert.assertEquals(1000000, names.size());
        Assert.assertEquals("Mr. Paul", names.get(0));

        Mockito.verify(names, Mockito.times(1)).size();
    }
}
