import scala.collection.compat._
      val range1Count = if (matcher.group(2) != null) { matcher.group(2) }
      else { "1" }
      val range2Count = if (matcher.group(4) != null) { matcher.group(4) }
      else { "1" }
        .map(_.split(" ").to(Seq))