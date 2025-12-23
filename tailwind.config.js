/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'yell': {
          100: '#FDFAE7',
          300: '#F0E491', // Your original color
          500: '#D9C54F',
          700: '#B4A02B',
          900: '#847315',
        },
        'gr1': {
          100: '#F3F6E2',
          300: '#D5DDAD',
          500: '#BBC863', // Your original color
          700: '#92A13A',
          900: '#687320',
        },
        'gr2': {
          100: '#E2EBE0',
          300: '#A9C0A1',
          500: '#7F9E75',
          700: '#658C58', // Your original color
          900: '#406336',
        },
        'gr3': {
          100: '#D9E3DE',
          300: '#84B09C',
          500: '#548B73',
          700: '#31694E', // Your original color
          900: '#19422F',
        },
      },
      fontFamily: {
        // Here, 'sans' is the key for the font-sans utility class.
        // We are overriding Tailwind's default sans-serif fonts.
        sans: ['nature-font', 'sans-serif'],
      },
    },
  },
  plugins: [],
}

