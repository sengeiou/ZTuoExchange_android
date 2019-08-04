package cn.ztuo.app;

import android.content.Context;

import cn.ztuo.data.DataRepository;
import cn.ztuo.data.LocalDataSource;
import cn.ztuo.data.RemoteDataSource;


/**
 * Created by Administrator on 2017/9/25.
 */

public class Injection {
    public static DataRepository provideTasksRepository(Context context) {
        return DataRepository.getInstance(RemoteDataSource.getInstance(), LocalDataSource.getInstance(context));
    }
}
