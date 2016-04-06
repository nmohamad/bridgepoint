/**
 * generated by Xtext 2.9.1
 */
package org.xtuml.bp.ui.xtext.myDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>assign Statement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.xtuml.bp.ui.xtext.myDsl.assignStatement#getLhs <em>Lhs</em>}</li>
 *   <li>{@link org.xtuml.bp.ui.xtext.myDsl.assignStatement#getRhs <em>Rhs</em>}</li>
 * </ul>
 *
 * @see org.xtuml.bp.ui.xtext.myDsl.MyDslPackage#getassignStatement()
 * @model
 * @generated
 */
public interface assignStatement extends EObject
{
  /**
   * Returns the value of the '<em><b>Lhs</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Lhs</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Lhs</em>' containment reference.
   * @see #setLhs(expression)
   * @see org.xtuml.bp.ui.xtext.myDsl.MyDslPackage#getassignStatement_Lhs()
   * @model containment="true"
   * @generated
   */
  expression getLhs();

  /**
   * Sets the value of the '{@link org.xtuml.bp.ui.xtext.myDsl.assignStatement#getLhs <em>Lhs</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Lhs</em>' containment reference.
   * @see #getLhs()
   * @generated
   */
  void setLhs(expression value);

  /**
   * Returns the value of the '<em><b>Rhs</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Rhs</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Rhs</em>' containment reference.
   * @see #setRhs(expression)
   * @see org.xtuml.bp.ui.xtext.myDsl.MyDslPackage#getassignStatement_Rhs()
   * @model containment="true"
   * @generated
   */
  expression getRhs();

  /**
   * Sets the value of the '{@link org.xtuml.bp.ui.xtext.myDsl.assignStatement#getRhs <em>Rhs</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Rhs</em>' containment reference.
   * @see #getRhs()
   * @generated
   */
  void setRhs(expression value);

} // assignStatement
