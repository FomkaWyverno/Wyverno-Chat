package ua.wyverno.natives.keyboard;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.util.ExceptionToString;

import java.util.HashSet;
import java.util.Set;

public class NativeKeyboard implements NativeKeyListener {

    private static final Logger logger = LoggerFactory.getLogger(NativeKeyboard.class);

    private final Set<Integer> buttonPressedSet = new HashSet<>();

    private final Set<NativeKeyboardPressScript> keyPressScripts = new HashSet<>();
    private final Set<NativeKeyboardReleasedScript> keyReleasedScripts = new HashSet<>();

    private static NativeKeyboard instance;

    private NativeKeyboard() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            logger.debug("Register native hook");
        } catch (NativeHookException e) {
            logger.error(ExceptionToString.getString(e));
        }
    }

    public static NativeKeyboard getInstance() {
        if (instance == null) instance = new NativeKeyboard();
        return instance;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        int keyCode = event.getKeyCode();
        if (!buttonPressedSet.contains(keyCode)) {
            //logger.trace("NativeKeyPressed: " + NativeKeyEvent.getKeyText(keyCode));
            buttonPressedSet.add(keyCode);
            keyPressScripts.forEach(e -> e.keyPress(event));
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {
        int keyCode = event.getKeyCode();
        if (buttonPressedSet.contains(keyCode)) {
            //logger.trace("NativeKeyReleased: " + NativeKeyEvent.getKeyText(keyCode));
            buttonPressedSet.remove(keyCode);
            keyReleasedScripts.forEach(e -> e.keyReleased(event));
        }
    }

    public void addScript(NativeKeyboardPressScript script) {
        this.keyPressScripts.add(script);
    }

    public void addScript(NativeKeyboardReleasedScript script) {
        this.keyReleasedScripts.add(script);
    }

    public void removeScript(NativeKeyboardPressScript script) {
        this.keyPressScripts.remove(script);
    }

    public void removeScript(NativeKeyboardReleasedScript script) {
        this.keyReleasedScripts.remove(script);
    }
}
