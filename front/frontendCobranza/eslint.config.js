// @ts-check
const eslint = require('@eslint/js');
const tseslint = require('typescript-eslint');
const angular = require('angular-eslint');

module.exports = tseslint.config(
  {
    ignores: ['dist/**', 'coverage/**', '.angular/**', 'node_modules/**'],
  },
  {
    files: ['**/*.ts'],
    extends: [
      eslint.configs.recommended,
      ...tseslint.configs.recommended,
      ...angular.configs.tsRecommended,
    ],
    processor: angular.processInlineTemplates,
    rules: {
      // --- carried over from the old tslint.json / src/tslint.json ---
      '@angular-eslint/directive-selector': [
        'error',
        { type: 'attribute', prefix: 'app', style: 'camelCase' },
      ],
      '@angular-eslint/component-selector': [
        'error',
        { type: 'element', prefix: 'app', style: 'kebab-case' },
      ],
      '@angular-eslint/component-class-suffix': 'error',
      '@angular-eslint/directive-class-suffix': 'error',
      '@angular-eslint/no-input-rename': 'error',
      '@angular-eslint/no-output-rename': 'error',
      '@angular-eslint/no-output-on-prefix': 'error',
      '@angular-eslint/use-lifecycle-interface': 'error',
      '@angular-eslint/use-pipe-transform-interface': 'error',
      '@angular-eslint/no-inputs-metadata-property': 'error',
      '@angular-eslint/no-outputs-metadata-property': 'error',

      '@typescript-eslint/member-ordering': [
        'error',
        {
          default: ['static-field', 'instance-field', 'static-method', 'instance-method'],
        },
      ],
      '@typescript-eslint/no-inferrable-types': ['error', { ignoreParameters: true }],
      '@typescript-eslint/no-non-null-assertion': 'error',
      '@typescript-eslint/no-use-before-define': 'error',

      'no-restricted-imports': ['error', { paths: ['rxjs/Rx'] }],
      'max-len': ['error', { code: 140 }],
      'no-console': [
        'error',
        {
          allow: [
            'log',
            'warn',
            'error',
            'dir',
            'table',
            'assert',
            'count',
            'countReset',
            'group',
            'groupCollapsed',
            'groupEnd',
            'timeLog',
            'timeStamp',
          ],
        },
      ],
      'no-fallthrough': 'error',
      'quote-props': ['error', 'as-needed'],
      quotes: ['error', 'single'],

      // This app deliberately keeps its single NgModule architecture and
      // constructor injection; these two rules push the opposite way and would
      // otherwise flag every component in the project.
      '@angular-eslint/prefer-standalone': 'off',
      '@angular-eslint/prefer-inject': 'off',
      // Switching to OnPush would change when the views re-render; these
      // components mutate their arrays in place and rely on default change
      // detection.
      '@angular-eslint/prefer-on-push-component-change-detection': 'off',

      // --- rules tslint:recommended had switched off for this project ---
      '@typescript-eslint/explicit-member-accessibility': 'off',
      '@typescript-eslint/array-type': 'off',
      '@typescript-eslint/no-empty-function': 'off',
      '@typescript-eslint/no-require-imports': 'off',
      'max-classes-per-file': 'off',
      'arrow-parens': 'off',
      'sort-imports': 'off',
      'comma-dangle': 'off',
      'no-empty': 'off',
    },
  },
  {
    files: ['**/*.html'],
    extends: [...angular.configs.templateRecommended],
    rules: {},
  }
);
