module.exports = {
    parser: "@typescript-eslint/parser",
    parserOptions: {
        project: './tsconfig.json'
    },
    plugins: [
        "@typescript-eslint",
        "eslint-comments",
        "jest",
        "promise"
    ],
    extends: [
        "airbnb-typescript",
        "plugin:@typescript-eslint/recommended",
        "plugin:eslint-comments/recommended",
        "plugin:react-hooks/recommended",
        "plugin:jest/recommended",
        "plugin:promise/recommended",
        "prettier",
        "prettier/react",
        "prettier/@typescript-eslint",
    ],
    env: {
        node: true,
        browser: true,
        jest: true,
    },
    rules: {
        "jsx-a11y/no-autofocus": "off",
        "no-prototype-builtins": "off",
        "import/prefer-default-export": "off",
        "import/no-default-export": "error",
        "react/destructuring-assignment": "off",
        "react/jsx-filename-extension": "off",
        "react/jsx-props-no-spreading": "off",
        "no-use-before-define": [
            "error",
            {functions: false, classes: true, variables: true},
        ],
        "@typescript-eslint/explicit-function-return-type": [
            "error",
            {allowExpressions: true, allowTypedFunctionExpressions: true},
        ],
        "@typescript-eslint/no-use-before-define": [
            "error",
            {functions: false, classes: true, variables: true, typedefs: true},
        ],
        "@typescript-eslint/no-explicit-any": "off",
        "@typescript-eslint/explicit-module-boundary-types": [
            "error",
            {allowArgumentsExplicitlyTypedAsAny: true}
        ],
        "@typescript-eslint/no-unused-vars": [
            "error",
            {argsIgnorePattern: "^_"}
        ],
    },
}
