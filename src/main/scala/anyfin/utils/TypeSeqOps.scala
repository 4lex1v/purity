/*
 * Copyright 2017 Aleksandr Ivanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package anyfin.utils

import scala.meta.Type
import scala.collection.mutable
import scala.collection.immutable.Seq
import scala.meta._

object TypeSeqOps {

  /**
   * Since scalameta doesn't have a structural equality, only referential, i.e:
   *
   * {{{
   *   scala> Type.Name("F") == Type.Name("F")
   *   res0: false
   * }}}
   *
   * to remove duplicate types from the list need to do it manually.
   *
   * @param types list of types with possible duplicates.
   * @return list of types without duplicates
   */
  def removeDuplicates (types: Seq[Type]): Seq[Type] = {
    val seen = mutable.HashSet.empty[String]
    types.foldRight(Seq.empty[Type]) { case (elem, acc) =>
      if (!seen(elem.structure)) {
        seen += elem.structure
        elem +: acc
      } else acc
    }
  }

  /**
   * Adaptation of the [[List]]'s diff function for scalameta.
   *
   * @param origin list of original types
   * @param sub    list of types to subtract from origin
   */
  def diffTypes (origin: Seq[Type], sub: Seq[Type]): Seq[Type] = {
    val index = indexTypeStructOccur(sub)
    origin.foldRight(Seq.empty[Type]) { case (elem, acc) =>
      val ox = index(elem.structure)
      if (ox == 0) elem +: acc
      else {
        index(elem.structure) = ox - 1
        acc
      }
    }
  }

  private def indexTypeStructOccur (types: Seq[Type]): mutable.Map[String, Int] = {
    val occ = new mutable.HashMap[String, Int] { override def default(s: String) = 0 }
    for { tpe <- types } occ(tpe.structure) += 1
    occ
  }

}
