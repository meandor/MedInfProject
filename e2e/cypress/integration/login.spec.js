context('login', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('should login', () => {
    cy.url().should('include', '/login');

    cy.get('input[data-testid=email]')
      .type('fake@email.com')
      .should('have.value', 'fake@email.com');

    cy.get('input[data-testid=password]')
      .type('password')
      .should('have.value', 'password')
      .type('{enter}');

    cy.url().should('include', '/dashboard');
  })
})
