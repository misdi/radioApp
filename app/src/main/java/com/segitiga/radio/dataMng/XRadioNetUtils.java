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

package com.segitiga.radio.dataMng;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.segitiga.radio.constants.IXRadioConstants;
import com.segitiga.radio.model.GenreModel;
import com.segitiga.radio.model.RadioModel;
import com.segitiga.radio.model.ThemeModel;
import com.segitiga.radio.model.UIConfigModel;
import com.segitiga.radio.ypylibs.model.ResultModel;
import com.segitiga.radio.ypylibs.utils.DownloadUtils;
import com.segitiga.radio.ypylibs.utils.StringUtils;
import com.segitiga.radio.ypylibs.utils.YPYLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * @author:YPY Global
 * @Email: bl911vn@gmail.com
 * @Website: www.segitiga.com
 * @Date:Oct 20, 2017
 */

public class XRadioNetUtils implements IXRadioConstants {

    private static final String FORMAT_API_END_POINT= "/api/api.php?method=%1$s";
    private static final String METHOD_GET_GENRES= "getGenres";
    private static final String METHOD_GET_RADIOS= "getRadios";
    private static final String METHOD_GET_THEMES= "getThemes";
    private static final String METHOD_GET_REMOTE_CONFIGS= "getRemoteConfigs";

    public static final String METHOD_PRIVACY_POLICY= "/privacy_policy.php";
    public static final String METHOD_TERM_OF_USE= "/term_of_use.php";

    private static final String KEY_API= "&api_key=";
    private static final String KEY_QUERY= "&q=";
    private static final String KEY_GENRE_ID= "&genre_id=";
    private static final String KEY_APP_TYPE= "&app_type=";
    private static final String KEY_OFFSET= "&offset=";
    private static final String KEY_LIMIT= "&limit=";
    private static final String KEY_IS_FEATURE= "&is_feature=1";

    public static final String FOLDER_GENRES= "/uploads/genres/";
    public static final String FOLDER_RADIOS= "/uploads/radios/";
    public static final String FOLDER_THEMES= "/uploads/themes/";


    public static ResultModel<GenreModel> getListGenreModel(String urlHost,String apiKey){
        try{
            String url= urlHost + String.format(FORMAT_API_END_POINT, METHOD_GET_GENRES) +
                    KEY_API + apiKey;
            YPYLog.e(TAG,"==========>getListGenreModel="+url);
            Reader mInputStream = DownloadUtils.downloadReader(url);
            Type mTypeToken = new TypeToken<ResultModel<GenreModel>>(){}.getType();
            return JsonParsingUtils.getResultModel(mInputStream,mTypeToken);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static ResultModel<RadioModel> getListTopChartRadio(String urlHost, String apiKey,int offset,int limit){
        return getListRadioModel(urlHost,apiKey,-1,null,offset,limit,true);
    }

    public static ResultModel<RadioModel> getListRadioModel(String urlHost, String apiKey,long genreId,int offset,int limit){
        return getListRadioModel(urlHost,apiKey,genreId,null,offset,limit,false);
    }

    public static ResultModel<RadioModel> searchRadioModel(String urlHost, String apiKey,String query,int offset,int limit){
        return getListRadioModel(urlHost,apiKey,-1,query,offset,limit,false);
    }

    static ResultModel<RadioModel> getListRadioModel(String urlHost, String apiKey, int offset, int limit){
        return getListRadioModel(urlHost,apiKey,-1,null,offset,limit,false);
    }


    private static ResultModel<RadioModel> getListRadioModel(String urlHost, String apiKey, long genreId, String query, int offset, int limit, boolean isFeature){
        try{
            StringBuilder mStringBuilder =new StringBuilder(urlHost);
            mStringBuilder.append(String.format(FORMAT_API_END_POINT,METHOD_GET_RADIOS));
            mStringBuilder.append(KEY_API).append(apiKey);
            if(offset>=0){
                mStringBuilder.append(KEY_OFFSET).append(offset);
            }
            if(limit>0){
                mStringBuilder.append(KEY_LIMIT).append(limit);
            }
            if(genreId>0){
                mStringBuilder.append(KEY_GENRE_ID).append(genreId);
            }
            if(isFeature){
                mStringBuilder.append(KEY_IS_FEATURE);
            }
            if(!TextUtils.isEmpty(query)){
                mStringBuilder.append(KEY_QUERY).append(StringUtils.urlEncodeString(query));
            }
            String url=mStringBuilder.toString();
            YPYLog.e(TAG,"==========>getListRadioModel="+url);
            Type mTypeToken = new TypeToken<ResultModel<RadioModel>>(){}.getType();
            return getListDataFromServer(url,mTypeToken);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    static ResultModel<UIConfigModel> getUIConfigModel(String urlHost, String apiKey){
        try{
            String url= urlHost + String.format(FORMAT_API_END_POINT, METHOD_GET_REMOTE_CONFIGS) +
                    KEY_API + apiKey;
            YPYLog.e(TAG,"==========>getUIConfigModel="+url);
            Type mTypeToken = new TypeToken<ResultModel<UIConfigModel>>(){}.getType();
            return getListDataFromServer(url,mTypeToken);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    static ResultModel<ThemeModel> getDefaultThemes(String urlHost, String apiKey){
        return getListThemes(urlHost,apiKey,-1,-1,TYPE_APP_SINGLE);
    }

    public static ResultModel<ThemeModel> getListThemes(String urlHost,String apiKey,int offset,int limit,int appType){
        try{
            StringBuilder mStringBuilder =new StringBuilder(urlHost);
            mStringBuilder.append(String.format(FORMAT_API_END_POINT,METHOD_GET_THEMES));
            mStringBuilder.append(KEY_API).append(apiKey);
            if(offset>=0){
                mStringBuilder.append(KEY_OFFSET).append(offset);
            }
            if(limit>0){
                mStringBuilder.append(KEY_LIMIT).append(limit);
            }
            if(appType>0){
                mStringBuilder.append(KEY_APP_TYPE).append(appType);
            }
            String url=mStringBuilder.toString();
            YPYLog.e(TAG,"==========>getListThemes="+url);
            Type mTypeToken = new TypeToken<ResultModel<ThemeModel>>(){}.getType();
            return getListDataFromServer(url,mTypeToken);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static <T> ResultModel<T> getListDataFromServer(String url,Type mTypeToken){
        try {
            Reader mInputStream = DownloadUtils.downloadReader(url);
            return JsonParsingUtils.getResultModel(mInputStream,mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> ResultModel<T> getListDataFromAssets(Context mContext,String uri,Type mTypeToken){
        try {
            InputStream data =mContext.getAssets().open(uri);
            return JsonParsingUtils.getResultModel(new InputStreamReader(data),mTypeToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getImageOfSong(String title,String artist,String apiKey){
        try{
            if(!TextUtils.isEmpty(title)){
                String url;
                String urlImg;
                if(!TextUtils.isEmpty(artist)){
                    url =String.format(FORMAT_LAST_FM, StringUtils.urlEncodeString(title+" "+artist),apiKey);
                    urlImg = JsonParsingUtils.parsingImageSong(DownloadUtils.downloadString(url));
                    if(!TextUtils.isEmpty(urlImg)){
                        return processHtmlLink(urlImg);
                    }
                }
                url =String.format(FORMAT_LAST_FM, StringUtils.urlEncodeString(artist),apiKey);
                urlImg = JsonParsingUtils.parsingImageSong(DownloadUtils.downloadString(url));
                if(!TextUtils.isEmpty(urlImg)){
                    return processHtmlLink(urlImg);
                }
                else{
                    url =String.format(FORMAT_LAST_FM, StringUtils.urlEncodeString(title),apiKey);
                    urlImg = JsonParsingUtils.parsingImageSong(DownloadUtils.downloadString(url));
                    if(!TextUtils.isEmpty(urlImg)){
                        return processHtmlLink(urlImg);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String processHtmlLink(String url){
        try{
            if(url.startsWith("https://www.last.fm") && !url.toLowerCase().endsWith(".png") && !url.toLowerCase().endsWith(".jpg")){
                Document mDocument = Jsoup.connect(url).get();
                if(mDocument!=null){
                    Elements mListImgCover = mDocument.getElementsByClass(" js-video-preview-playlink video-preview-playlink");
                    if(mListImgCover!=null && mListImgCover.size()>0){
                        for(Element mElement:mListImgCover){
                            Elements mElementImgs = mElement.getElementsByClass("video-preview");
                            if(mElementImgs!=null && mElementImgs.size()>0){
                                for(Element mElement1: mElementImgs){
                                    Elements elementImg = mElement1.getElementsByTag("img");
                                    if(elementImg!=null && elementImg.size()>0){
                                        return elementImg.first().attr("src");
                                    }
                                }

                            }

                        }
                    }
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

}
