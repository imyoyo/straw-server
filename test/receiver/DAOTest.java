package receiver;

import models.CpuUsage;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DAOTest {

    public static final String GROUP = "UNIT_TEST_GROUP";

    @Test
    public void testInsertRawAndQueryRange() throws Exception {
        DAO.clearGroup(GROUP);
        for (int i = 0; i <= 10; i++)
            DAO.insertRaw(GROUP, "A", i * 1000, new CpuUsage(i*10, new ArrayList<Double>()));
        Map<String, Map<Long, CpuUsage>> map = DAO.getRawsBetween(GROUP, 2000, 7000);
        Assert.assertEquals(5, map.get("A").size());
        Assert.assertEquals(20, map.get("A").get(2000L).getTotal(), 0.0);
    }

    @Test
    public void testGroup() {
        DAO.removeGroupIfExist(GROUP);
        Assert.assertFalse(DAO.isGroupExist(GROUP));
        DAO.insertGroupIfNotExist(GROUP);
        Assert.assertTrue(DAO.isGroupExist(GROUP));
        DAO.removeGroupIfExist(GROUP);
        Assert.assertFalse(DAO.isGroupExist(GROUP));
    }
}