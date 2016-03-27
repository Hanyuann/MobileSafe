package junit.test;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.test.AndroidTestCase;
import cn.edu.sdu.mobilesafe.bean.BlackNumberInfo;
import cn.edu.sdu.mobilesafe.db.dao.BlackNumberDao;

//≤‚ ‘ ˝æ›ø‚
public class TestDb extends AndroidTestCase {
	@Override
	protected void setUp() throws Exception {
		Context mContext = getContext().getApplicationContext();
		super.setUp();
	}

	public void testAdd() {
		BlackNumberDao dao = new BlackNumberDao(mContext);
		Random random = new Random();
		for (int i = 0; i < 40; i++) {
			Long number = 13300000000l + i;
			dao.add(number + "", String.valueOf(random.nextInt(3) + 1));
		}
	}

	public void testDelete() {
		BlackNumberDao dao = new BlackNumberDao(mContext);
		boolean delete = dao.delete("13300000000");
		assertEquals(true, delete);
	}

	public void testFind() {
		BlackNumberDao dao = new BlackNumberDao(mContext);
		String number = dao.findNumber("13300000004");
		System.out.println(number);
	}

	public void testFindAll() {
		BlackNumberDao dao = new BlackNumberDao(mContext);
		List<BlackNumberInfo> blackNumberInfos = dao.findAll();
		for (BlackNumberInfo blackNumberInfo : blackNumberInfos) {
			System.out.println(blackNumberInfo.getMode() + ""
					+ blackNumberInfo.getNumber());
		}
	}
}
