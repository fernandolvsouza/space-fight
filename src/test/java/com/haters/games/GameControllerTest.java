package com.haters.games;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GameControllerTest {
    
    private GameController controller;
    private Thread gameThread;

    @Before
    public void setUp() {
        controller = new GameController();
        gameThread = Mockito.mock(Thread.class);
    }

    @Test
    public void shouldStartGameLoop() throws InterruptedException {
        // When
        controller.start();
        Thread.sleep(100); // Give some time for the game loop to start
        
        // Then
        assertThat(controller.getFps()).isGreaterThan(0);
    }

    @Test
    public void shouldMaintainTargetFrameRate() throws InterruptedException {
        // When
        controller.start();
        Thread.sleep(1000); // Wait for 1 second to let the frame rate stabilize
        
        // Then
        float fps = controller.getFps();
        assertThat(fps).isBetween(55f, 65f); // Allow some variance around 60 FPS
    }
} 