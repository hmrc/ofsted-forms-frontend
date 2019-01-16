package uk.gov.hmrc.ofstedformsfrontend.forms.sc1

import org.w3c.dom.{Document, Node}
import uk.gov.hmrc.ofstedformsfrontend.marshallers.xml.XmlMarshaller

trait Individual

object Individual {

}

case class KnownIndividual(id: Int) extends Individual

object KnownIndividual {
  implicit val marshaller: XmlMarshaller[KnownIndividual] = new XmlMarshaller[KnownIndividual] {
    override def marshall(obj: KnownIndividual)(implicit document: Document): Node = {
      createFragment(document){
        _.createValue("AlreadyKnownOfsted", "Yes")
          .createValue("IndividualId", obj.id)
      }
    }
  }
}

case class UnknownIndividual(EY2Enclosed: Boolean,
                             title: Title) extends Individual

object UnknownIndividual {

}

