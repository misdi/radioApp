package com.segitiga.radio.firebasemsg;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * @author:YPY Global
 * @Email: bl911vn@gmail.com
 * @Website: http://segitiga.com
 * Created by YPY Global on 6/9/18.
 */
public class XRadioJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
