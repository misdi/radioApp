/*
 * Copyright (c) 2017. YPY Global - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://ypyglobal.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.segitiga.radio.ypylibs.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * @author:YPY Global
 * @Email: bl911vn@gmail.com
 * @Website: www.segitiga.com
 */
public class StringUtils {
	
	public static final String TAG = StringUtils.class.getSimpleName();

	public static String urlEncodeString(String data){
		if(data!=null && !data.equals("")){
			try {
				return URLEncoder.encode(data,"UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	public static String urlDecodeString(String data){
		if(data!=null && !data.equals("")){
			try {
				return URLDecoder.decode(data, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	

}
