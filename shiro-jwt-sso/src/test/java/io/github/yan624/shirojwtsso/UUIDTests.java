package io.github.yan624.shirojwtsso;

import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-24
 */
public class UUIDTests {
    @Test
    void uuidTest() {
        final UUID uuid = UUID.randomUUID();
        System.out.println(uuid);
    }
}
