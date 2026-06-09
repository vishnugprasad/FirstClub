/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#17211b',
        cream: '#f7f4ea',
        club: {
          50: '#f1f7f1',
          100: '#dcebdc',
          500: '#39764c',
          600: '#2d603d',
          700: '#244d32',
          900: '#173321',
        },
      },
      boxShadow: {
        soft: '0 18px 60px rgba(23, 51, 33, 0.10)',
      },
    },
  },
  plugins: [],
}
