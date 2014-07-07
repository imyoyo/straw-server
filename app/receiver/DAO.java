package receiver;

import com.mongodb.*;
import models.CpuUsage;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DAO {

    static {
        getRawCollection().createIndex(new BasicDBObject("time",1));
    }

    public static final String TEST_GROUP = "ce3bd840-f0a7-11e3-ac10-0800200c9a66";

	public static void insertGroupIfNotExist(String groupKey) {
        BasicDBObject q = new BasicDBObject();
        q.append("key", groupKey);
        BasicDBObject o = new BasicDBObject();
        o.append("key", groupKey);
        getGroupCollection().update(q, o, true, false);
    }

    private static DBCollection getGroupCollection() {
        return getDb().getCollection("group");
    }

    public static boolean isGroupExist(String groupKey) {
        BasicDBObject ref = new BasicDBObject();
        ref.append("key", groupKey);
        return getGroupCollection().find(ref).count() == 1;
    }

    public static void removeGroupIfExist(String groupKey) {
        BasicDBObject o = new BasicDBObject();
        o.append("key", groupKey);
        getGroupCollection().remove(o);
    }

    public static void insertRaw(String groupKey, String memberId, long time, CpuUsage  cpu) {
        BasicDBObject doc = new BasicDBObject();
        doc.append("time", time);
        doc.append("groupKey", groupKey);
        doc.append("memberId", memberId);
	    BasicDBObject cpuObj = new BasicDBObject();
	    cpuObj.append("total", cpu.getTotal());
	    BasicDBList list = new BasicDBList();
	    for(double x : cpu.getIndividual())
	    	list.add(x);
	    cpuObj.append("individual", list);
	    doc.append("cpu_v2", cpuObj);
	    doc.append("cpu", cpu.getTotal());
        getRawCollection().insert(doc);
    }

    public static void clearGroup(String groupKey) {
        getRawCollection().remove(new BasicDBObject("groupKey", new BasicDBObject("$eq", groupKey)));
    }

    public static Map<String, Map<Long, CpuUsage>> getRawsBetween(String groupKey, long startTime, long endTime) {
        BasicDBObject query = new BasicDBObject();
        query.append("groupKey", new BasicDBObject("$eq", groupKey));
        query.append("time", new BasicDBObject("$gte", startTime).append("$lt", endTime));

        HashMap<String, Map<Long, CpuUsage>> r = new HashMap<>();
        try (DBCursor cur = getRawCollection().find(query)) {
            while (cur.hasNext()) {
                DBObject obj = cur.next();
			double total;
			ArrayList<Double> individual = new ArrayList<>();
			if(obj.containsField("cpu_v2")) {
				DBObject cpuObj = (DBObject) obj.get("cpu_v2");
				total = (Double) cpuObj.get("total");
				ArrayList<Double> array = (ArrayList<Double>) cpuObj.get("individual");
				for(double  v : array) {
					individual.add(v);
				};
			} else {
				total = (Double) obj.get("cpu");
			}
                String memberId = (String) obj.get("memberId");
                if (!r.containsKey(memberId))
                    r.put(memberId, new HashMap<Long, CpuUsage>());
                r.get(memberId).put((Long) obj.get("time"), new CpuUsage(total, individual));
            }
        }
        return r;
    }

    private static DBCollection getRawCollection() {
        return getDb().getCollection("raw");
    }

    private static MongoClient client = null;

    private synchronized static DB getDb() {
        if (client == null) {
            try {
                client =  new MongoClient("straw.imyoyo.net", 27017);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        return client.getDB(DBName.DB_NAME);
    }

}
