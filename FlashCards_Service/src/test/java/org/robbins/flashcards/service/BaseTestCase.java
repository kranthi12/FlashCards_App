package org.robbins.flashcards.service;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

public class BaseTestCase {
    @Before public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
