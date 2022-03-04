package com.example.weather.slice;

import com.example.weather.ResourceTable;
import com.example.weather.data.CityMo;
import com.example.weather.data.ListItemProvider;
import com.example.weather.net.HiNet;
import com.example.weather.net.IHiNet;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Image;
import ohos.agp.components.ListContainer;
import ohos.agp.components.Text;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import org.devio.hi.json.HiJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Main ability slice.
 */
public class MainAbilitySlice extends AbilitySlice implements ListItemProvider.OnItemClickListener {
    private DirectionalLayout mLayout = new DirectionalLayout(this);
    private ListContainer listContainer;
    private ListItemProvider listItemProvider;
    private ArrayList<CityMo> cities = new ArrayList<>();
    private HiNet hiNet;
    private Text temperatureText, weatherText, tipsText, temperature_1Text, weather_1Text, tips_1Text;
    private Image weatherImage, weatherImage_1;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        hiNet = new HiNet();
        initLayout();
        initLayout1();
        intCity();
        loadData(new CityMo("深圳", "114.03,22.32"));/*此处的地址应由API根据经纬度解的，而经纬度应有设备自身获取*/
        loadData1(new CityMo("太原", "112.45,38.02"));
    }

    private void initLayout1() {
        listContainer = (ListContainer) findComponentById(ResourceTable.Id_list);
        listItemProvider = new ListItemProvider(this, this);
        listContainer.setItemProvider(listItemProvider);
        temperatureText = (Text) findComponentById(ResourceTable.Id_temperature_1);
        weatherText = (Text) findComponentById(ResourceTable.Id_weather_1);
        tipsText = (Text) findComponentById(ResourceTable.Id_tips_1);
        weatherImage = (Image) findComponentById(ResourceTable.Id_weather_icon_1);
    }

    private void intCity() {
        listItemProvider.setData(cities);
    }


    private void initLayout() {
        listContainer = (ListContainer) findComponentById(ResourceTable.Id_list);
        listItemProvider = new ListItemProvider(this, this);
        listContainer.setItemProvider(listItemProvider);
        temperature_1Text = (Text) findComponentById(ResourceTable.Id_temperature);
        weather_1Text = (Text) findComponentById(ResourceTable.Id_weather);
        tips_1Text = (Text) findComponentById(ResourceTable.Id_tips);
        weatherImage_1 = (Image) findComponentById(ResourceTable.Id_weather_icon);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    public void OnItemClick(CityMo cityMo, int position) {

        loadData(cityMo);
    }

    private void loadData(CityMo mo) {
        //https://devapi.qweather.com/v7/weather/now?location=114.03,22.32&key=<用户key:18418bf60e874597abf85b7906d03e48>
        Map<String, String> params = new HashMap<>();
        params.put("location", mo.cityCode);
        params.put("key", "你的和风天气API密钥");
        hiNet.get("https://devapi.qweather.com/v7/weather/now", params, new IHiNet.NetListener() {
            @Override
            public void onSuccess(HiJson res) {
                HiLog.info(new HiLogLabel(HiLog.LOG_APP, 0x00201, "WeatherApp"), "Get success:" + res.toString());
                //System.out.println("WeatherApp onSuccess:"+ res);
                bindData(res, mo);
            }

            @Override
            public void onFail(String message) {
                HiLog.info(new HiLogLabel(HiLog.LOG_APP, 0x00201, "WeatherApp"), "Get fail:" + message);
                //System.out.println("WeatherApp onSuccess:"+ message);
            }
        });
    }

    private void loadData1(CityMo mo) {
        //https://devapi.qweather.com/v7/weather/now?location=114.03,22.32&key=<用户key:18418bf60e874597abf85b7906d03e48>
        Map<String, String> params = new HashMap<>();
        params.put("location", mo.cityCode);
        params.put("key", "你的和风天气API密钥");
        hiNet.get("https://devapi.qweather.com/v7/weather/now", params, new IHiNet.NetListener() {
            @Override
            public void onSuccess(HiJson res) {
                HiLog.info(new HiLogLabel(HiLog.LOG_APP, 0x00201, "WeatherApp"), "Get success:" + res.toString());
                //System.out.println("WeatherApp onSuccess:"+ res);
                bindData1(res, mo);
            }

            @Override
            public void onFail(String message) {
                HiLog.info(new HiLogLabel(HiLog.LOG_APP, 0x00201, "WeatherApp"), "Get fail:" + message);
                //System.out.println("WeatherApp onSuccess:"+ message);
            }
        });
    }

    private void bindData(HiJson res, CityMo mo) {
        HiJson hiJson = res.get("now");
        String temperature = hiJson.value("temp");
        String weather = hiJson.value("text");
        String winddirection = hiJson.value("windDir");
        String windpower = hiJson.value("windScale");
        String humidity = hiJson.value("humidity");
        /*
         *上方为对于获取到的JSON数据的读取处理，可加异常抛出
         */
        temperatureText.setText(temperature + "℃");
        weatherText.setText(weather);
        tipsText.setText(mo.cityName + ":" + winddirection + " 最大风力:" + windpower + "级" + " 空气湿度" + humidity + "%");
        int id = ResourceTable.Media_sunshine;
        if (weather.contains("阴")) {
            id = ResourceTable.Media_overcast;
        } else if (weather.contains("雨")) {
            id = ResourceTable.Media_rain;
        } else if (weather.contains("雪")) {
            id = ResourceTable.Media_snow;
        } else if (weather.contains("雷电")) {
            id = ResourceTable.Media_thunder;
        } else if (weather.contains("多云")) {
            id = ResourceTable.Media_cloudy;
        }/*此处的天气详情待完善*/
        weatherImage.setImageAndDecodeBounds(id);
    }

    private void bindData1(HiJson res, CityMo mo) {
        HiJson hiJson = res.get("now");
        String temperature = hiJson.value("temp");
        String weather = hiJson.value("text");
        String winddirection = hiJson.value("windDir");
        String windpower = hiJson.value("windScale");
        String humidity = hiJson.value("humidity");
        /*
         *上方为对于获取到的JSON数据的读取处理，可加异常抛出
         */
        temperature_1Text.setText(temperature + "℃");
        weather_1Text.setText(weather);
        tips_1Text.setText(mo.cityName + ":" + winddirection + " 最大风力:" + windpower + "级" + " 空气湿度" + humidity + "%");
        int id = ResourceTable.Media_sunshine;
        if (weather.contains("阴")) {
            id = ResourceTable.Media_overcast;
        } else if (weather.contains("雨")) {
            id = ResourceTable.Media_rain;
        } else if (weather.contains("雪")) {
            id = ResourceTable.Media_snow;
        } else if (weather.contains("雷电")) {
            id = ResourceTable.Media_thunder;
        } else if (weather.contains("多云")) {
            id = ResourceTable.Media_cloudy;
        }/*此处的天气详情待完善*/
        weatherImage_1.setImageAndDecodeBounds(id);
    }
}