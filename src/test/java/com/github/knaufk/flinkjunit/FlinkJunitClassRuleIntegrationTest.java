package com.github.knaufk.flinkjunit;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.ConnectException;

import static org.assertj.core.api.Assertions.assertThat;

public class FlinkJunitClassRuleIntegrationTest {

  private static Configuration config = new Configuration();

  static {
    config.setInteger(ConfigConstants.LOCAL_NUMBER_TASK_MANAGER, 1);
    config.setInteger(ConfigConstants.TASK_MANAGER_NUM_TASK_SLOTS, 4);
    config.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER, false);

    config.setInteger(TaskManagerOptions.TASK_MANAGER_HEAP_MEMORY, 80);
    config.setBoolean(ConfigConstants.FILESYSTEM_DEFAULT_OVERWRITE_KEY, true);
    config.setString(ConfigConstants.AKKA_ASK_TIMEOUT, "10 s");
    config.setString(ConfigConstants.AKKA_STARTUP_TIMEOUT, "60 s");
  }

  @ClassRule public static final FlinkJUnitRule flinkRule = new FlinkJUnitRule(config);

  @Rule public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testFlinkUiNotReachable() throws IOException {

    thrown.expect(ConnectException.class);
    TestUtils.getOverviewJsonFromWebUi(flinkRule.getFlinkUiPort());
  }

  @Test
  public void whenFlinkUiIsNoWebPortIsNotReturned() throws IOException {
    assertThat(flinkRule.getFlinkUiPort()).isEqualTo(-1);
  }

  @Test
  public void testStreamEnv() throws Exception {

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    DataStream<Integer> testStream =
        env.fromElements(1, 2, 3, 4, 24, 2, 3, 23, 1, 3, 1, 13, 1, 31, 1);

    testStream.print();

    env.execute();
  }

  @Test
  public void testBatchEvn() throws Exception {

    ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    DataSource<Integer> testSet = env.fromElements(1, 2, 3, 4, 24, 2, 3, 23, 1, 3, 1, 13, 1, 31, 1);

    testSet.print();
  }
}
