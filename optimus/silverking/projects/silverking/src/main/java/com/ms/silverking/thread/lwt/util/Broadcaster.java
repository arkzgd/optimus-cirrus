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
package com.ms.silverking.thread.lwt.util;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.ms.silverking.thread.lwt.BaseWorker;

public class Broadcaster<T> extends BaseWorker<T> {
  private Set<Listener<T>> listeners;

  public Broadcaster() {
    this.listeners = new ConcurrentSkipListSet<>();
  }

  public void addListener(Listener<T> listener) {
    listeners.add(listener);
  }

  public void notifyListeners(T message) {
    addWork(message, 0);
  }

  @Override
  public void doWork(T notification) {
    for (Listener<T> listener : listeners) {
      listener.notification(this, notification);
    }
  }
}