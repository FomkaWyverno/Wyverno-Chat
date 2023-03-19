package ua.wyverno.natives.keyboard;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class NativeKeyboard implements NativeKeyListener {

    private static final Logger logger = LoggerFactory.getLogger(NativeKeyboard.class);



    private final Set<Integer> buttonPressedSet = new HashSet<>();

    private final Set<NativeKeyboardScript> nativeKeyboardFunctions = new HashSet<>();

    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        int keyCode = event.getKeyCode();
        if (!buttonPressedSet.contains(keyCode)) {
            logger.trace("NativeKeyPressed: " + NativeKeyEvent.getKeyText(keyCode));
            buttonPressedSet.add(keyCode);
            nativeKeyboardFunctions.forEach(e -> e.keyPress(event));
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {
        int keyCode = event.getKeyCode();
        if (buttonPressedSet.contains(keyCode)) {
            logger.trace("NativeKeyReleased: " + NativeKeyEvent.getKeyText(keyCode));
            buttonPressedSet.remove(keyCode);
            nativeKeyboardFunctions.forEach(e -> e.keyRelease(event));
        }
    }


}
