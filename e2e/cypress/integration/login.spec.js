context('login', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('should have login form', () => {
    cy.url().should('include', '/login')
    cy.get('#email')
      .type('fake@email.com')
      .should('have.value', 'fake@email.com')
  })
})
