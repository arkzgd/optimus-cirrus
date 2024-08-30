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
package optimus.platform.pickling

import optimus.graph.AlreadyCompletedNode
import optimus.graph.Node
import optimus.platform.annotations.nodeSync

import scala.annotation.implicitNotFound

@implicitNotFound(msg = "Type ${T} does not seem to be supported as a stored property (can't find unpickler).")
trait Unpickler[T] {
  @nodeSync def unpickle(pickled: Any, ctxt: PickledInputStream): T

  // provide a default implementation of this to allow for synchronous overrides of unpickle (which may not make
  // further asynchronous calls anyway and often doesn't benefit from being an @node)
  def unpickle$queued(pickled: Any, ctxt: PickledInputStream): Node[T] =
    new AlreadyCompletedNode(unpickle(pickled, ctxt))
}