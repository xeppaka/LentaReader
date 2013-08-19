package cz.fit.lentaruand.utils;

import android.os.StrictMode;

public class LentaUtils {
	public static void strictMode() {
		if (LentaConstants.DEVELOPER_MODE) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites()
					.detectNetwork().penaltyLog().build());
			
			StrictMode.VmPolicy.Builder vmBuilder = new StrictMode.VmPolicy.Builder();
			vmBuilder = vmBuilder.detectLeakedSqlLiteObjects().detectLeakedClosableObjects();
			
			if (LentaConstants.SDK_VER > 10) {
				vmBuilder = vmBuilder.detectLeakedClosableObjects();
			}
			
			StrictMode.setVmPolicy(vmBuilder.penaltyLog().penaltyDeath().build());
		}
	}
}
