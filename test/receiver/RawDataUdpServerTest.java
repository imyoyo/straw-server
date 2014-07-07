package receiver;

import models.CpuUsage;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class RawDataUdpServerTest {
    @Test
    public void testSending() throws Exception {
        DAO.clearGroup("TEST_G");
        RawDataUdpServer.processData("{\"cpuUsage\":{\"unit\":[0.06,0.02], \"total\": 0.04},\"memoryUsage\":{\"max\":2147012608, \"current\":1287536640},\"groupKey\":\"TEST_G\",\"machineId\":\"my pc\"}", 10 * 1000);
        Thread.sleep(100);
        Map<String, Map<Long, CpuUsage>> map = DAO.getRawsBetween("TEST_G", 0, 100 * 1000);
        Assert.assertEquals(0.04, (Double)map.get("my pc").get(10*1000L).getTotal(), 0);
    }

    @Test
    public void testDisk() throws Exception {
        DAO.clearGroup("TEST_G");
        RawDataUdpServer.processData("{\"disk\":[{ \"name\": \"C\", \"size\": 104857595904, \"current\": 99579379712},{ \"name\": \"D\", \"size\": 46136291328, \"current\": 32863764480},{ \"name\": \"E\", \"size\": 104697163776, \"current\": 20720062464},{ \"name\": \"F\", \"size\": 109587726336, \"current\": 44097179648},{ \"name\": \"G\", \"size\": 862638305280, \"current\": 474228486144}],\"groupKey\":\"ce3bd840-f0a7-11e3-ac10-0800200c9a66\",\"machineId\":\"thedual\"}", 10*1000);
        Thread.sleep(100);
    }

}
