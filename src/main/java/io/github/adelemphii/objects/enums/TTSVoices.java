package io.github.adelemphii.objects.enums;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;

public enum TTSVoices {

    ENGLISH_AU_A("en-AU-Wavenet-A", "en-AU", SsmlVoiceGender.FEMALE),
    ENGLISH_AU_B("en-AU-Wavenet-B", "en-AU", SsmlVoiceGender.MALE),
    ENGLISH_AU_C("en-AU-Wavenet-C", "en-AU", SsmlVoiceGender.FEMALE),
    ENGLISH_AU_D("en-AU-Wavenet-D", "en-AU", SsmlVoiceGender.MALE),
    ENGLISH_US_A("en-US-Wavenet-A", "en-US", SsmlVoiceGender.FEMALE),
    ENGLISH_US_B("en-US-Wavenet-B", "en-US", SsmlVoiceGender.MALE),
    ENGLISH_US_C("en-US-Wavenet-C", "en-US", SsmlVoiceGender.FEMALE),
    ENGLISH_US_D("en-US-Wavenet-D", "en-US", SsmlVoiceGender.MALE);

    private final String name;
    private final String languageCode;
    private final SsmlVoiceGender gender;

    TTSVoices(String name, String languageCode, SsmlVoiceGender gender) {
        this.name = name;
        this.languageCode = languageCode;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public SsmlVoiceGender getGender() {
        return gender;
    }

    public static TTSVoices getVoiceFromName(String name) {
        for(TTSVoices voice : TTSVoices.values()) {
            if(voice.getName().equals(name)) {
                return voice;
            }
        }
        return null;
    }
}
