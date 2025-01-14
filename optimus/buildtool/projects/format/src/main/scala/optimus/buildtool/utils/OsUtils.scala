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
package optimus.buildtool.utils

import scala.collection.compat._
import scala.collection.immutable.Seq
import scala.sys.process.Process
import scala.sys.process.ProcessLogger
import scala.util.Try

object OsUtils {
  val Linux6Version = "linux-el6.x86_64"
  val Linux7Version = "linux-el7.x86_64"
  val Linux8Version = "linux-el8_9.x86_64"
  val Linux8_10Version = "linux-el8_10.x86_64"
  val WindowsVersion = "windows-10.0"

  val Linux6SysName: String =
    sys.props.get("optimus.buildtool.sysName.linux6").getOrElse("x86_64.linux.2.6.glibc.2.12")
  val Linux7SysName: String =
    sys.props.get("optimus.buildtool.sysName.linux7").getOrElse("x86_64.linux.2.6.glibc.2.17")
  val Linux8SysName: String =
    sys.props.get("optimus.buildtool.sysName.linux8").getOrElse("x86_64.linux.2.6.glibc.2.28")
  val WindowsSysName: String =
    sys.props.get("optimus.buildtool.sysName.windows").getOrElse("x86_64.nt.win10")

  private val osVersionMapping = Map(
    Linux6Version -> Linux6SysName,
    Linux7Version -> Linux7SysName,
    Linux8Version -> Linux8SysName,
    Linux8_10Version -> Linux8SysName,
    WindowsVersion -> WindowsSysName
  )

  def isWindows: Boolean = osType == "windows"

  def isWindows(osVersion: String): Boolean = osVersion.split('-').head == "windows"

  def osType: String = sys.props("os.name").split(' ').head.toLowerCase
  def osType(osVersion: String): String = osVersion.split('-').head

  def osVersion: String =
    if (isWindows) s"$osType-${sys.props("os.version")}" // eg. "windows-10.0"
    else linuxOsVersion(osType, sys.props("os.version")) // eg. "linux-el7.x86_64", "linux-el6.x86_64"

  private[utils] def linuxOsVersion(tpe: String, version: String): String =
    s"$tpe-${version.split('.').takeRight(2).mkString(".")}"

  lazy val sysName: Seq[String] = {
    var n: Seq[String] = Nil
    Try {
      Process(Seq("fs", "sysname")) ! ProcessLogger { s =>
        n = readSysName(s)
      }
    }
    n
  }

  def exec: String = osVersionMapping.getOrElse(
    OsUtils.osVersion,
    throw new IllegalArgumentException(s"Unrecognized OS version: ${OsUtils.osVersion}"))

  private val sysNameRegex = s"'([^']+)'".r
  private[utils] def readSysName(output: String): Seq[String] =
    sysNameRegex.findAllMatchIn(output).map(_.group(1)).to(Seq)

}
