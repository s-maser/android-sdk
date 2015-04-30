package io.relayr.ble.service;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class LongWriteDataParserTest {

    @Test public void writeEmptyPackageTest() {
        LongWriteDataParser parser = new LongWriteDataParser("".getBytes());
        byte[] first = parser.getData();

        assertThat(first.length).isEqualTo(4);
        checkAllEmpty(first, 0, 1, 2, 3);

        byte[] second = parser.getData();
        assertThat(second.length).isEqualTo(0);
    }

    @Test public void writeShortPackageTest() {
        LongWriteDataParser parser = new LongWriteDataParser("123".getBytes());
        byte[] first = parser.getData();

        assertThat(first.length).isEqualTo(7);
        assertThat((int) first[0]).isEqualTo(0);
        assertThat((int) first[1]).isEqualTo(0);

        assertThat((int) first[2]).isEqualTo(3);
        assertThat((int) first[3]).isEqualTo(0);

        checkNotEpmty(first, 4, 5, 6);

        byte[] second = parser.getData();
        assertThat(second).isEmpty();
    }

    @Test public void writeOneFullPackageTest() {
        LongWriteDataParser parser = new LongWriteDataParser("11111111111111".getBytes());
        byte[] first = parser.getData();

        assertThat(first.length).isEqualTo(18);
        assertThat((int) first[0]).isEqualTo(0);
        assertThat((int) first[1]).isEqualTo(0);

        assertThat((int) first[2]).isEqualTo(14);
        assertThat((int) first[3]).isEqualTo(0);

        byte[] second = parser.getData();
        assertThat(second).isEmpty();
    }

    @Test public void writeLongPackageTest() {
        LongWriteDataParser parser = new LongWriteDataParser("12345678911111abc".getBytes());
        byte[] first = parser.getData();

        assertThat(first.length).isEqualTo(18);
        assertThat((int) first[0]).isEqualTo(0);
        assertThat((int) first[1]).isEqualTo(0);

        assertThat((int) first[2]).isEqualTo(17);
        assertThat((int) first[3]).isEqualTo(0);

        byte[] second = parser.getData();

        assertThat(second.length).isEqualTo(5);
        assertThat((int) second[0]).isEqualTo(14);
        assertThat((int) second[1]).isEqualTo(0);
    }

    private void checkAllEmpty(byte[] data, int... positions) {
        for (int position : positions)
            assertThat((int) data[position]).isEqualTo(0);
    }

    private void checkNotEpmty(byte[] data, int... positions) {
        for (int position : positions)
            assertThat((int) data[position]).isNotZero();
    }
}
