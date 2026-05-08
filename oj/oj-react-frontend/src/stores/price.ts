import { create } from 'zustand'

type PriceStore = {
  applyDiscount: (discount: number) => void
  doublePrice: number
  price: number
  setDoublePrice: (doublePrice: number) => void
  setPrice: (price: number) => void
}

function createPriceState(price: number) {
  return {
    doublePrice: price * 2,
    price,
  }
}

export const usePriceStore = create<PriceStore>()((set) => ({
  ...createPriceState(10),
  applyDiscount: (discount) => {
    set((state) => createPriceState(state.price - discount))
  },
  setDoublePrice: (doublePrice) => {
    set(createPriceState(doublePrice / 2))
  },
  setPrice: (price) => {
    set(createPriceState(price))
  },
}))
