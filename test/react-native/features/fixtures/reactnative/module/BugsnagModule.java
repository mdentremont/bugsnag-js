package com.reactnative.module;


import java.util.HashSet;
import java.util.Set;

import android.util.Log;

import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.Configuration;
import com.bugsnag.android.EndpointConfiguration;
import com.bugsnag.android.Logger;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NoSuchKeyException;

import com.reactnative.scenarios.Scenario;

public class BugsnagModule extends ReactContextBaseJavaModule {
  private static ReactApplicationContext reactContext;

  private ScenarioFactory factory = new ScenarioFactory();

  BugsnagModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
  }

  @Override
  public String getName() {
    return "BugsnagTestInterface";
  }

  @ReactMethod
  public void runScenario(String scenarioName, Callback completeCallback) {
      Scenario testScenario = factory.testScenarioForName(scenarioName, reactContext);
      testScenario.run();
      completeCallback.invoke();
  }

  @ReactMethod
  public void startBugsnag(ReadableMap options, Callback readyCallback) {
    Configuration bugsnagConfig = createConfiguration(options);
    bugsnagConfig.setLogger(new Logger() {
      private static final String TAG = "Bugsnag";

      @Override
      public void e(String msg) {
          Log.e(TAG, msg);
      }

      @Override
      public void e(String msg, Throwable throwable) {
          Log.e(TAG, msg, throwable);
      }

      @Override
      public void w(String msg) {
          Log.w(TAG, msg);
      }

      @Override
      public void w(String msg, Throwable throwable) {
          Log.w(TAG, msg, throwable);
      }

      @Override
      public void i(String msg) {
          Log.i(TAG, msg);
      }

      @Override
      public void i(String msg, Throwable throwable) {
          Log.i(TAG, msg, throwable);
      }

      @Override
      public void d(String msg) {
          Log.d(TAG, msg);
      }

      @Override
      public void d(String msg, Throwable throwable) {
          Log.d(TAG, msg, throwable);
      }
    });
    Bugsnag.start(reactContext, bugsnagConfig);
    readyCallback.invoke();
  }

  private Configuration createConfiguration(ReadableMap options) {
      Configuration config = new Configuration(options.getString("apiKey"));
      config.setAutoTrackSessions(options.getBoolean("autoTrackSessions"));

      if (options.hasKey("endpoint")) {
        config.setEndpoints(new EndpointConfiguration(options.getString("endpoint"), options.getString("endpoint")));
      }
      else if (options.hasKey("endpoints")) {
          ReadableMap endpoints = options.getMap("endpoints");
          config.setEndpoints(new EndpointConfiguration(endpoints.getString("notify"), endpoints.getString("sessions")));
      }

      if (options.hasKey("appVersion")) {
        String appVersion = null;
        appVersion = options.getString("appVersion");
        config.setAppVersion(appVersion);
      }

      if (options.hasKey("appType")) {
        String appType = options.getString("appType");
        config.setAppType(appType);
      }

      if (options.hasKey("releaseStage")) {
        String releaseStage = options.getString("releaseStage");
        config.setReleaseStage(releaseStage);
      }

      if (options.hasKey("enabledReleaseStages")) {
        Set<String> enabledReleaseStages = new HashSet<String>();
        ReadableArray ar = options.getArray("enabledReleaseStages");
        for (int i = 0; i < ar.size(); i++) enabledReleaseStages.add(ar.getString(i));
        config.setEnabledReleaseStages(enabledReleaseStages);
      }

      if (options.hasKey("redactedKeys")) {
        Set<String> redactedKeys = new HashSet<String>();
        ReadableArray rkAr = options.getArray("redactedKeys");
        for (int i = 0; i < rkAr.size(); i++) redactedKeys.add(rkAr.getString(i));
        config.setEnabledReleaseStages(redactedKeys);
      }

      try {
        ReadableMap configMetaData = options.getMap("configMetaData");
        config.addMetadata("nativedata", configMetaData.toHashMap());
      } catch (NoSuchKeyException e) {
        // ignore NoSuchKeyException
      }

      return config;
  }
}
