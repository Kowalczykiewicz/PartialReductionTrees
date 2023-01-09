package com.partial_reduction_trees.core

import com.partial_reduction_trees._
import zio._
import zio.test._
import zio.test.Assertion._

object ScanSpec extends ZIOSpecDefault:
  def spec = suite("ScanSpec")(
    test("Scan works correctly") {
      //TODO add more generic tests
      val input = Array[Float](1, 4, 3, 40, 5, 44, 12, 44, 22, 66, 11, 12, 112, 300)
      val outputSequential = Array.ofDim[Float](input.length)
      val outputParallel = Array.ofDim[Float](input.length)
      Sequential.scan(input, outputSequential)
      for {
        _    <- Parallel.scan(input, outputParallel, 12)
      } yield assertTrue(outputSequential.sameElements(outputParallel))
    }
    //TODO add scalameter tests
  )
end ScanSpec