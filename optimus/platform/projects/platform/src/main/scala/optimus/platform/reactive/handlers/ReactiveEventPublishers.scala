/*
 * Morgan Stanley makes this available to you under the Apache License, Version 2.0 (the "License").
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * See the NOTICE file distributed with this work for additional information regarding copyright ownership.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package optimus.platform.reactive.handlers

import optimus.graph.tracking.EventCause
import optimus.platform.BusinessEvent
import optimus.platform.ContainedEvent
import optimus.platform.storable.Entity

object GlobalStatusSource extends ReactiveEventPublisher[GlobalStatusEvent] with WithPublicPublish[GlobalStatusEvent] {
  def startImpl: Unit = ()
  def stopImpl: Unit = ()
}

class StatusEventSource extends ReactiveEventPublisher[StateChangeEvent] with WithPublicPublish[StateChangeEvent] {
  def startImpl: Unit = ()
  def stopImpl: Unit = ()
}

trait WithStartStopImpl {
  def startImpl = {
    beforeStart()
    doStart()
    afterStart()
  }
  def stopImpl = {
    beforeStop()
    doStop()
    afterStop()
  }
  def beforeStop() = ()
  def doStop() = ()
  def afterStop() = ()
  def beforeStart() = ()
  def doStart() = ()
  def afterStart() = ()
}
trait WithStatusStartStop extends WithStartStopImpl {
  val status = new StatusEventSource()

  override def beforeStart() = {
    super.beforeStart()
    status.publish(StateChangeEvent.StatusInitialising)
  }
  override def afterStart() = {
    super.afterStart()
    status.publish(StateChangeEvent.StatusStarted)
  }
  override def beforeStop() = {
    super.beforeStop()
    status.publish(StateChangeEvent.StatusStopped)
  }
  def setError(t: Throwable, cause: EventCause) = status.publish(StateChangeEvent.StatusError(t, cause))
  def clearError() = status.publish(StateChangeEvent.StatusOperational)
}

sealed trait WithStatusSource[T <: ReactiveEvent] extends ReactiveEventPublisher[T] with WithStatusStartStop {
  final override def statusEventSource: Option[StatusEventSource] = Some(status)
}

trait EventSource[T <: BaseReactiveEvent] extends WithStatusSource[T]

trait SignalAsEventSource[V] extends WithStatusSource[SignalAsEvent[V]]

trait ContainedEventSignalAsEventSource[V <: BusinessEvent with ContainedEvent]
    extends WithStatusSource[ContainedEventSignalAsEvent[V]]

trait TransactionSignalAsEventSource[V <: Entity] extends WithStatusSource[TransactionEntitySignalAsEvent[V]]