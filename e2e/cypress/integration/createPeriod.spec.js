context('createPeriod', () => {
  beforeEach(() => {
    cy.visit('/');

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

  function dateString(date) {
    return `${date.getFullYear()}-${date.getMonth()}-${date.getDate()}`;
  }

  it('should create a period and delete it afterwards', () => {
    cy.visit('/create');

    const today = new Date();
    const in3Days = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 3);

    cy.get(`section[data-testid=${dateString(today)}]`)
        .click();

    cy.get(`section[data-testid=${dateString(in3Days)}]`)
        .click();

    cy.get('.create__actions .button-primary')
        .click();

    cy.url().should('include', '/dashboard');

    cy.get(`section[data-testid=${dateString(today)}] .active`);

    cy.get(`section[data-testid=${dateString(in3Days)}] .active`)
        .click();

    cy.get('.detail__actions .button-secondary')
        .click();
  });
})
