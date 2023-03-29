package dev.shiro8613.missionplugin.utils.timer;

public enum TimerEnum {
    CountDown(CountDownTimer.class);

    private final Class<? extends Timer> implClass;

    TimerEnum(Class<? extends Timer> timerClass) {
        this.implClass = timerClass;
    }

    public Class<? extends Timer> getImplClass() {
        return implClass;
    }
}
