/*
 * © Copyright 2020
 * Author: Piyush Rana
 * Email: piyush@knoldus.com
 */

package runners

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.configuration.{Configuration, RestOptions}
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend
import org.apache.flink.runtime.state.StateBackend
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}

case class FlinkStateCheckpointConfig(
  interval: Long,
  pause: Int,
  timeout: Int,
  path: String,
  incremental: Boolean
)

case class TaskConfig(
  logLevel: String,
  parallelism: Int,
  restartStrategy: String,
  bufferTimeout: Option[Int],
  stateCheckpoints: FlinkStateCheckpointConfig,
  useLocalMode: Option[Boolean],
  localModePort : Option[Int]
)

object FlinkRunner {

  def apply(config: TaskConfig,
            backend: (FlinkStateCheckpointConfig) => StateBackend): Either[Exception, StreamExecutionEnvironment] = {

    val runner = config.useLocalMode match {
      case Some(v) =>
        if (v) {
          val conf = new Configuration
          config.localModePort.foreach(port => conf.setInteger(RestOptions.PORT, port))
          StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf)
        } else {
          StreamExecutionEnvironment.getExecutionEnvironment
        }
      case None => StreamExecutionEnvironment.getExecutionEnvironment
    }

    config.bufferTimeout.map(t => runner.setBufferTimeout(t))

    if (config.restartStrategy == "none") {
      runner.setRestartStrategy(RestartStrategies.noRestart())
    }

    runner.enableCheckpointing(config.stateCheckpoints.interval)
    runner.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    runner.getCheckpointConfig.setMinPauseBetweenCheckpoints(config.stateCheckpoints.pause)
    runner.getCheckpointConfig.setCheckpointTimeout(config.stateCheckpoints.timeout)
    runner.setStateBackend(backend(config.stateCheckpoints))

    runner.setParallelism(config.parallelism)
    runner.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    Right(runner)
  }
}

object backend {
  def apply(checkpointConfig: FlinkStateCheckpointConfig): StateBackend =
    new RocksDBStateBackend(checkpointConfig.path.trim, checkpointConfig.incremental)
}
