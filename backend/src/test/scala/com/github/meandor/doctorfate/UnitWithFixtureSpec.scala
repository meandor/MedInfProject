package com.github.meandor.doctorfate
import org.mockito.IdiomaticMockito
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers

trait UnitWithFixtureSpec extends FixtureAnyFeatureSpec with Matchers with IdiomaticMockito
