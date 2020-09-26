import React, { ChangeEvent } from 'react';

export function updateState(
  setStateFn: React.Dispatch<React.SetStateAction<string>>
): (event: React.ChangeEvent<HTMLInputElement>) => void {
  return (event: ChangeEvent<HTMLInputElement>) => {
    event.preventDefault();
    setStateFn(event.target.value);
  };
}
