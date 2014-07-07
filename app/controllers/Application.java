package controllers;

import models.CpuUsage;
import play.libs.Json;
import play.mvc.*;

import receiver.DAO;
import views.html.*;

import java.util.*;

public class Application extends Controller {

	public static Result intro() {
		return ok(intro.render());
	}

	public static Result index() {
		return ok(index.render("STRAW !!!"));
	}

	public static Result recent() {
		long now = System.currentTimeMillis();
		int bucketSize = 10;
		long lastBucketStartTime = (now - 1000) / 1000 * 1000;
		Map<String, Map<Long, CpuUsage>> map = DAO.getRawsBetween(DAO.TEST_GROUP, now - (bucketSize + 2) * 1000, now);

		ArrayList<Object> list = new ArrayList<>();
		for (String memberId : new TreeSet<String>(map.keySet())) {
			Double[] total = new Double[bucketSize];
			for (int i = 0; i < bucketSize; i++)
				total[i] = -1.0;
			long lastTime = 0;
			List<Double> last = null;
			for (long time : map.get(memberId).keySet()) {
				long bucketStartTime = time / 1000 * 1000;
				int index = (bucketSize - 1) - (int) (lastBucketStartTime - bucketStartTime) / 1000;
				if (0 <= index && index < bucketSize) {
					CpuUsage cpuUsage = map.get(memberId).get(time);
					total[index] = cpuUsage.getTotal();
					if (lastTime == 0 || lastTime < time) {
						lastTime = time;
						last = cpuUsage.getIndividual();
					}
				}
			}
			HashMap<Object, Object> oneMemberMap = new HashMap<>();
			oneMemberMap.put("name", memberId);
			oneMemberMap.put("cpu", total);
			if (last != null)
				oneMemberMap.put("lastIndividual", last);
			list.add(oneMemberMap);
		}
		Map<Object, Object> result = new HashMap<>();
		result.put("list", list);
		result.put("lastBucketTime", lastBucketStartTime);
		return ok(Json.toJson(result));
	}

}
